      async function sendHomeMessage() {
        if (state.isSendingMessage) return;
        const text = els.homeComposer.value.trim();
        if (!text) return;
        const cwd = '/home/sunsw/code';
        state.isSendingMessage = true;
        els.homeSend.disabled = true;
        let threadId = '';
        try {
          const sessionOptions = buildSessionOptions();
          const result = await api('POST', '', { cwd, name: text.substring(0, 30), ...sessionOptions });
          threadId = result && result.claudeId ? result.claudeId : '';
          if (!threadId) throw new Error('create session did not return thread id');
          state.resumedThreadIds.add(threadId);
          els.homeComposer.value = '';
          resetTextareaHeight(els.homeComposer);
          state.recentlyCreatedThreadIds.add(threadId);
          setTimeout(() => state.recentlyCreatedThreadIds.delete(threadId), 15000);
          selectThread(threadId);
          await delay(500);
          await api('POST', '/' + encodeURIComponent(threadId) + '/message', { text });
          state.liveOverlay = { activityLabel: '思考中…', reasoningText: '', errorText: '' };
          setThreadInProgress(threadId, true);
          updateThreadSendButton();
          renderConversation();
          await loadMessages(threadId);
          await refreshThreads();
        } catch (e) {
          alert('发送失败: ' + e.message);
          if (threadId && state.selectedThreadId === threadId) {
            selectThread('');
          }
        } finally {
          state.isSendingMessage = false;
          els.homeSend.textContent = '发送';
          els.homeSend.disabled = false;
        }
      }
      async function sendThreadMessage() {
        if (state.isSendingMessage) return;
        const text = els.threadComposer.value.trim();
        if (!text || !state.selectedThreadId) return;
        await ensureThreadResumed(state.selectedThreadId);
        state.isSendingMessage = true;
        els.threadSend.disabled = true;
        try {
          const msgOptions = buildMessageOptions();
          await api('POST', '/' + encodeURIComponent(state.selectedThreadId) + '/message', { text, ...msgOptions });
          els.threadComposer.value = '';
          resetTextareaHeight(els.threadComposer);
          state.liveOverlay = { activityLabel: '思考中…', reasoningText: '', errorText: '' };
          setThreadInProgress(state.selectedThreadId, true);
          state.isSendingMessage = false;
          updateThreadSendButton();
          // Append user message directly to preserve historical messages in view.
          // Do NOT call loadMessages() here — it would fetch only in-memory messages
          // (missing history) while the session is inProgress. Full history is loaded
          // automatically when the streaming completes (complete/error event).
          state.messages.push({ id: 'u_' + Date.now(), role: 'user', text: text, messageType: 'userMessage' });
          renderConversation();
          await refreshThreads();
          await fetchPendingRequests();
        } catch (e) {
          const msg = e && e.message ? e.message : String(e);
          if (msg.includes('thread not found') || msg.includes('Session not found')) {
            alert('该会话在远程服务器上已失效，可能是连接断开后重建了。');
            setThreadInProgress(state.selectedThreadId, false);
            state.liveOverlay = null;
            state.isSendingMessage = false;
            await refreshThreads();
            const stillExists = state.knownThreadIds && state.knownThreadIds.has(state.selectedThreadId);
            if (!stillExists) selectThread('');
            else updateThreadSendButton();
          } else {
            alert('发送失败: ' + msg);
            state.isSendingMessage = false;
            updateThreadSendButton();
          }
        }
      }
      async function doInterrupt() {
        if (!state.selectedThreadId || state.isInterrupting) return;
        state.isInterrupting = true;
        els.threadSend.textContent = '停止中…';
        els.threadSend.disabled = true;
        try {
          await api('POST', '/' + encodeURIComponent(state.selectedThreadId) + '/cancel', {});
          setThreadInProgress(state.selectedThreadId, false);
          state.liveOverlay = null;
          await loadMessages(state.selectedThreadId);
          await refreshThreads();
        } catch (e) {
          alert('停止失败: ' + e.message);
        } finally {
          state.isInterrupting = false;
          updateThreadSendButton();
        }
      }
      async function handleThreadSendClick() {
        const allThreads = state.projectGroups.flatMap(g => g.threads);
        const thread = allThreads.find(t => t.id === state.selectedThreadId);
        if (thread && thread.inProgress) {
          await doInterrupt();
        } else {
          await sendThreadMessage();
        }
      }
      async function archiveThread(threadId) {
        if (!threadId) return;
        if (!confirm('Archive this thread?')) return;
        try {
          await api('DELETE', '/' + encodeURIComponent(threadId));
          if (state.selectedThreadId === threadId) selectThread('');
          await refreshThreads();
        } catch (e) {
          alert('Archive failed: ' + e.message);
        }
      }
      async function respondApproval(id, value) {
        let payload;
        if (value === 'accept') payload = { result: { approved: true } };
        else if (value === 'acceptForSession') payload = { result: { approved: true, forSession: true } };
        else if (value === 'decline') payload = { error: { message: '用户拒绝了该请求' } };
        else if (value === 'cancel') payload = { error: { message: '用户取消了该请求' } };
        else payload = { result: {} };
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id, ...payload }) });
          removePendingRequest(id);
          await refreshThreads();
        } catch (e) { alert('操作失败：' + e.message); }
      }
      async function respondToolInput(id) {
        const answers = {};
        document.querySelectorAll('[data-action="question-answer"][data-req="' + id + '"]').forEach(sel => {
          const qid = sel.getAttribute('data-qid');
          answers[qid] = sel.value;
        });
        document.querySelectorAll('[data-action="question-other"][data-req="' + id + '"]').forEach(inp => {
          const qid = inp.getAttribute('data-qid');
          if (answers[qid] === 'Other') answers[qid] = inp.value || 'Other';
        });
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id, result: { answers } }) });
          removePendingRequest(id);
        } catch (e) { alert('操作失败：' + e.message); }
      }
      async function respondToolFail(id) {
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id, error: { message: '用户拒绝了工具调用' } }) });
          removePendingRequest(id);
        } catch (e) { alert('操作失败：' + e.message); }
      }
      async function respondToolSuccess(id) {
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id, result: {} }) });
          removePendingRequest(id);
        } catch (e) { alert('操作失败：' + e.message); }
      }
      async function respondToolAllow(id) {
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ requestId: id, allow: true }) });
          removePendingRequest(id);
        } catch (e) { alert('操作失败：' + e.message); }
      }
      async function respondToolDecline(id) {
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ requestId: id, allow: false }) });
          removePendingRequest(id);
        } catch (e) { alert('操作失败：' + e.message); }
      }
      async function respondEmpty(id) {
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id, result: {} }) });
          removePendingRequest(id);
        } catch (e) { alert('操作失败：' + e.message); }
      }
      async function respondReject(id) {
        try {
          await fetch('/claude-api/server-requests/respond', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id, error: { message: '用户拒绝了该请求' } }) });
          removePendingRequest(id);
        } catch (e) { alert('操作失败：' + e.message); }
      }

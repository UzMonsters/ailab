import { Send, X } from "lucide-react";
import { type AssistantMessage } from "./hooks/useAssistant";
import { useState } from "react";

export function AssistantPanel({
  messages,
  onSend,
  onExecuteAction,
  onClose,
  isTeamChat = false,
}: {
  messages: AssistantMessage[];
  onSend: (text: string) => void;
  onExecuteAction: (action: string, payload: Record<string, unknown>) => void;
  onClose: () => void;
  isTeamChat?: boolean;
}) {
  const [input, setInput] = useState("");

  const handleSend = () => {
    if (input.trim()) {
      onSend(input);
      setInput("");
    }
  };

  return (
    <section
      className="sandbox-assistant-panel fixed bottom-20 right-5 z-[80] flex h-[min(520px,70vh)] w-[min(360px,calc(100vw-2rem))] flex-col rounded-2xl border border-[var(--border)] bg-[var(--card)] shadow-2xl"
      aria-label="AI assistant"
    >
      <header className="flex items-center justify-between border-b border-[var(--border)] p-4">
        <div>
          <p className="font-semibold">{isTeamChat ? 'Командный чат' : 'Лабораторный ассистент'}</p>
          <p className="text-xs text-[var(--muted-foreground)]">
            {isTeamChat ? 'Сообщения сохраняются в workspace' : 'Подсказки и проверка установки'}
          </p>
        </div>
        <button
          className="touch-target"
          onClick={onClose}
          aria-label="Close assistant"
        >
          <X size={17} />
        </button>
      </header>
      <div className="flex-1 space-y-3 overflow-y-auto p-4 flex flex-col">
        {messages.map((message, index) => (
          <div key={index} className={`flex flex-col ${message.role === "user" ? "items-end" : "items-start"}`}>
            <p
              className={`max-w-[88%] rounded-xl px-3 py-2 text-sm ${message.role === "user" ? "bg-blue-600 text-white" : "bg-gray-100 dark:bg-gray-800"}`}
            >
              {message.content}
            </p>
            {message.actions && message.actions.length > 0 && (
              <div className="mt-2 flex flex-wrap gap-2">
                {message.actions.map((action, i) => (
                  <button
                    key={i}
                    className="rounded-lg bg-blue-100 dark:bg-blue-900/30 px-2 py-1 text-xs text-blue-700 dark:text-blue-300 hover:bg-blue-200 dark:hover:bg-blue-900/50"
                    onClick={() => onExecuteAction(action.action, action.payload)}
                  >
                    {action.label}
                  </button>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
      <div className="flex gap-2 border-t border-[var(--border)] p-3">
        <input
          aria-label="Ask AI assistant"
          value={input}
          onChange={(event) => setInput(event.target.value)}
          onKeyDown={(event) => event.key === "Enter" && handleSend()}
          className="min-w-0 flex-1 rounded-xl border border-[var(--border)] bg-transparent px-3 outline-none"
          placeholder="Спросите об эксперименте…"
        />
        <button
          className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-600 text-white hover:bg-blue-700"
          onClick={handleSend}
          aria-label="Send"
        >
          <Send size={16} />
        </button>
      </div>
    </section>
  );
}

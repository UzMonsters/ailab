"use client";

import { useState } from "react";
import { usePathname } from "next/navigation";
import { EquipmentThumbnail } from "@/entities/equipment/ui/EquipmentRendererRegistry";

type PracticeQuizProps = {
  number: string;
  title: string;
  goal?: string;
  equipmentHint?: string;
  question: string;
  answers: string[];
  correctIndex: number;
  feedbackCorrect: string;
  feedbackWrong: string;
  illustrationEquipmentId?: string;
  onNext?: () => void;
};

export function PracticeQuiz({
  number,
  title,
  goal,
  equipmentHint,
  question,
  answers,
  correctIndex,
  feedbackCorrect,
  feedbackWrong,
  illustrationEquipmentId,
  onNext,
}: PracticeQuizProps) {
  const [selected, setSelected] = useState<number | null>(null);
  const [checked, setChecked] = useState(false);
  const lang = usePathname().split('/')[1] || 'en';

  const handleSelect = (index: number) => {
    if (checked) return;
    setSelected(index);
  };

  const isAnswered = checked && selected !== null;
  const isCorrect = selected === correctIndex;
  const labels = lang === 'ru'
    ? { check: 'Проверить ответ', retry: 'Попробовать снова', next: 'Следующий вопрос', correct: 'Верно', wrong: 'Не совсем', score: 'Результат', xp: '+10 XP' }
    : lang === 'uz'
    ? { check: 'Javobni tekshirish', retry: 'Qayta urinib ko‘rish', next: 'Keyingi savol', correct: 'To‘g‘ri', wrong: 'Unchalik emas', score: 'Natija', xp: '+10 XP' }
    : { check: 'Check answer', retry: 'Try again', next: 'Next question', correct: 'Correct', wrong: 'Not quite', score: 'Score', xp: '+10 XP' };

  return (
    <div className="practice-container page-layout-practice">
      <span className="practice-number codex-mono">{number}</span>
      <h2 className="practice-header codex-handwriting">{title}</h2>

      {goal && (
        <p className="practice-goal codex-serif">{goal}</p>
      )}

      {illustrationEquipmentId && (
        <div className="practice-illustration">
          <EquipmentThumbnail type={illustrationEquipmentId} size={90} />
        </div>
      )}

      {equipmentHint && (
        <p className="practice-equipment-hint codex-mono">{equipmentHint}</p>
      )}

      <p className="practice-question codex-serif">{question}</p>

      <div className="practice-options">
        {answers.map((answer, i) => {
          let cls = "practice-option";
          if (isAnswered) {
            if (i === correctIndex) cls += " selected-correct";
            else if (i === selected) cls += " selected-wrong";
          }

          return (
            <button
              key={i}
              className={cls}
              onClick={() => handleSelect(i)}
              disabled={checked}
            >
              <span className="practice-checkbox">
                {!isAnswered && <span className={selected === i ? 'practice-radio-dot active' : 'practice-radio-dot'} />}
                {isAnswered && i === correctIndex && "✓"}
                {isAnswered && i === selected && i !== correctIndex && "✗"}
              </span>
              <span className="practice-option-text codex-serif">{answer}</span>
            </button>
          );
        })}
      </div>

      {!checked && selected !== null && <button className="action-btn codex-mono practice-check-btn" onClick={() => setChecked(true)}>{labels.check}</button>}

      {isAnswered && (
        <div
          className={`practice-feedback codex-handwriting ${
            isCorrect ? "practice-feedback-correct" : "practice-feedback-wrong"
          }`}
        >
          <strong>{isCorrect ? `✓ ${labels.correct}` : `✕ ${labels.wrong}`}</strong>
          <span>{isCorrect ? feedbackCorrect : feedbackWrong}</span>
          <div className="practice-result-line codex-mono"><span>{labels.score}: {isCorrect ? '1 / 1' : '0 / 1'}</span>{isCorrect && <span>{labels.xp}</span>}</div>
          <button className="action-btn codex-mono practice-next-btn" onClick={() => { if (isCorrect) onNext?.(); else { setChecked(false); setSelected(null); } }}>{isCorrect ? labels.next : labels.retry}</button>
        </div>
      )}
    </div>
  );
}

// ─── Practice Step Sorter ─────────────────────────────────────────────────────
// For Practice 04: arrange steps in correct order

type StepSorterProps = {
  number: string;
  title: string;
  question: string;
  steps: string[];
  correctOrder: number[];
  feedback: string;
};

export function PracticeStepSorter({
  number,
  title,
  question,
  steps,
  correctOrder,
  feedback,
}: StepSorterProps) {
  const [order, setOrder] = useState<number[]>(steps.map((_, i) => i));
  const [checked, setChecked] = useState(false);

  const moveUp = (pos: number) => {
    if (pos === 0 || checked) return;
    const newOrder = [...order];
    [newOrder[pos - 1], newOrder[pos]] = [newOrder[pos], newOrder[pos - 1]];
    setOrder(newOrder);
  };

  const moveDown = (pos: number) => {
    if (pos === order.length - 1 || checked) return;
    const newOrder = [...order];
    [newOrder[pos], newOrder[pos + 1]] = [newOrder[pos + 1], newOrder[pos]];
    setOrder(newOrder);
  };

  const isCorrect =
    order.every((val, idx) => val === correctOrder[idx]);

  return (
    <div className="practice-container page-layout-practice">
      <span className="practice-number codex-mono">{number}</span>
      <h2 className="practice-header codex-handwriting">{title}</h2>
      <p className="practice-question codex-serif">{question}</p>

      <div className="practice-step-list">
        {order.map((stepIndex, pos) => (
          <div key={stepIndex} className="practice-step-item">
            <span className="practice-step-num codex-mono">{pos + 1}</span>
            <span className="practice-step-text codex-serif">{steps[stepIndex]}</span>
            {!checked && (
              <div className="practice-step-controls">
                <button onClick={() => moveUp(pos)} disabled={pos === 0}>↑</button>
                <button onClick={() => moveDown(pos)} disabled={pos === order.length - 1}>↓</button>
              </div>
            )}
          </div>
        ))}
      </div>

      {!checked && (
        <button className="action-btn codex-mono practice-check-btn" onClick={() => setChecked(true)}>
          Проверить / Check
        </button>
      )}

      {checked && (
        <div
          className={`practice-feedback codex-handwriting ${
            isCorrect ? "practice-feedback-correct" : "practice-feedback-wrong"
          }`}
        >
          {isCorrect ? feedback : "Порядок неверный. Попробуйте ещё раз. / Wrong order. Try again."}
        </div>
      )}
    </div>
  );
}

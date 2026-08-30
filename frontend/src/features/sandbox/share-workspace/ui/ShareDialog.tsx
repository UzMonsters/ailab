"use client";

import { useState, useCallback } from "react";
import { X, Copy, Share2, Link2, QrCode, Lock, Eye, EyeOff, Check, ShieldCheck } from "lucide-react";
import type { Item, Connection } from "@/widgets/sandbox/types";
import type { SceneSnapshot } from "@/engine/scene/Scene";
import { generateQrSvg } from "@/shared/lib/qrCodeGenerator";

// ─── Snapshot schema ──────────────────────────────────────────────────────────

export interface SandboxSnapshot {
  version: 2;
  title: string;
  objects: SceneSnapshot['objects'];
  connections: SceneSnapshot['connections'];
  scenario: { id: string; step: number } | null;
  viewport: { zoom: number; panX: number; panY: number };
  createdAt: string;
  updatedAt: string;
}

// ─── Serialization helpers ───────────────────────────────────────────────────

export function serializeSnapshot(
  title: string,
  sceneSnapshot: SceneSnapshot,
  scenario: { id: string; step: number } | null,
  zoom: number,
  pan: { x: number; y: number }
): SandboxSnapshot {
  return {
    version: 2,
    title,
    objects: sceneSnapshot.objects,
    connections: sceneSnapshot.connections,
    scenario,
    viewport: { zoom, panX: pan.x, panY: pan.y },
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };
}

function encodeSnapshot(snapshot: SandboxSnapshot): string {
  try {
    return btoa(encodeURIComponent(JSON.stringify(snapshot)));
  } catch {
    return "";
  }
}

function decodeSnapshot(encoded: string): SandboxSnapshot | null {
  try {
    return JSON.parse(decodeURIComponent(atob(encoded))) as SandboxSnapshot;
  } catch {
    return null;
  }
}

const MAX_URL_BYTES = 50_000; // 50KB limit

/** Build a share URL from the current snapshot. Returns null if too large. */
export function buildShareUrl(snapshot: SandboxSnapshot): string | null {
  const encoded = encodeSnapshot(snapshot);
  if (encoded.length > MAX_URL_BYTES) return null;
  const base = typeof window !== "undefined" ? window.location.origin + window.location.pathname : "";
  return `${base}#snapshot=${encoded}`;
}

/** Parse snapshot from current URL hash. Returns null if not present or invalid. */
export function parseSnapshotFromHash(): SandboxSnapshot | null {
  if (typeof window === "undefined") return null;
  const hash = window.location.hash;
  const match = hash.match(/[#&]snapshot=([^&]*)/);
  if (!match) return null;
  return decodeSnapshot(match[1]);
}

/** Validate and normalize a raw snapshot, returning a safe version or null. */
export function normalizeSnapshot(raw: unknown): SandboxSnapshot | null {
  if (!raw || typeof raw !== "object") return null;
  const s = raw as Record<string, unknown>;
  if (s.version !== 2) return null;
  if (!Array.isArray(s.objects) || !Array.isArray(s.connections)) return null;
  return raw as SandboxSnapshot;
}

// ─── ShareDialog ──────────────────────────────────────────────────────────────

interface ShareDialogProps {
  snapshot: SandboxSnapshot;
  onClose: () => void;
}

export function ShareDialog({ snapshot, onClose }: ShareDialogProps) {
  const [copied, setCopied] = useState(false);
  const [accessRole, setAccessRole] = useState<"viewer" | "editor">("viewer");
  const [password, setPassword] = useState("");
  const [showPasswordInput, setShowPasswordInput] = useState(false);
  const [showPlainPassword, setShowPlainPassword] = useState(false);
  const [showQrCode, setShowQrCode] = useState(false);

  const baseShareUrl = buildShareUrl(snapshot);
  const shareUrl = baseShareUrl
    ? `${baseShareUrl}&role=${accessRole}${password.trim() ? `&pwd=${encodeURIComponent(password.trim())}` : ""}`
    : null;
  const isTooLarge = shareUrl === null;

  const copyLink = useCallback(async () => {
    if (!shareUrl) return;
    try {
      if (navigator.clipboard) {
        await navigator.clipboard.writeText(shareUrl);
        setCopied(true);
        setTimeout(() => setCopied(false), 2500);
      } else {
        window.prompt("Скопируйте ссылку:", shareUrl);
      }
    } catch {
      window.prompt("Скопируйте ссылку:", shareUrl);
    }
  }, [shareUrl]);

  const shareNative = useCallback(async () => {
    if (!shareUrl || !navigator.share) return;
    try {
      await navigator.share({
        title: snapshot.title || "Химический эксперимент",
        text: "Откройте мой химический эксперимент в лаборатории!",
        url: shareUrl,
      });
    } catch {
      // Ignored user cancel
    }
  }, [shareUrl, snapshot.title]);

  const qrSvgMarkup = shareUrl ? generateQrSvg(shareUrl, 220) : null;

  return (
    <div
      className="share-dialog-overlay fixed inset-0 z-[300] grid place-items-center bg-black/70 p-4 backdrop-blur-md transition-opacity"
      role="dialog"
      aria-modal="true"
      aria-labelledby="share-dialog-title"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="share-dialog w-full max-w-md animate-fade-in-up rounded-2xl border border-white/10 bg-[#0b0f19] p-6 shadow-[0_32px_80px_rgba(0,0,0,0.8)]">
        {/* Header */}
        <div className="mb-4 flex items-start justify-between">
          <div>
            <h2 id="share-dialog-title" className="text-base font-bold text-white flex items-center gap-2">
              <Share2 size={18} className="text-[var(--primary)]" />
              Общий доступ
            </h2>
            <p className="mt-0.5 text-[11px] text-white/50">
              {snapshot.title || "Эксперимент без названия"}
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Закрыть"
            className="rounded-lg p-1.5 text-white/40 transition-colors hover:bg-white/10 hover:text-white"
          >
            <X size={16} />
          </button>
        </div>

        {/* Disclaimer / Info */}
        <div className="mb-4 rounded-xl border border-cyan-500/20 bg-cyan-500/5 px-3.5 py-2.5">
          <p className="text-[11px] leading-relaxed text-cyan-200/80">
            Ссылка сохраняет полную <strong className="text-cyan-100">интерактивную копию</strong> лаборатории. Все приборы и вещества будут загружены автоматически.
          </p>
        </div>

        {/* Access Settings & Roles */}
        <div className="mb-4 space-y-3">
          <div className="flex flex-col gap-1.5">
            <label className="text-xs font-semibold text-white/70">Права доступа</label>
            <div className="grid grid-cols-2 gap-2 rounded-xl border border-white/10 bg-white/[0.02] p-1">
              <button
                type="button"
                onClick={() => setAccessRole("viewer")}
                className={`rounded-lg px-3 py-1.5 text-xs font-medium transition-all ${
                  accessRole === "viewer"
                    ? "bg-[var(--primary)] text-white shadow-md"
                    : "text-white/40 hover:text-white/80"
                }`}
              >
                Только просмотр
              </button>
              <button
                type="button"
                onClick={() => setAccessRole("editor")}
                className={`rounded-lg px-3 py-1.5 text-xs font-medium transition-all ${
                  accessRole === "editor"
                    ? "bg-[var(--primary)] text-white shadow-md"
                    : "text-white/40 hover:text-white/80"
                }`}
              >
                Редактирование
              </button>
            </div>
          </div>

          {/* Password Protection */}
          <div className="rounded-xl border border-white/10 bg-white/[0.02] p-3 space-y-2">
            <div className="flex items-center justify-between">
              <span className="flex items-center gap-1.5 text-xs font-semibold text-white/80">
                <Lock size={13} className="text-orange-400" />
                Защита паролем
              </span>
              <button
                type="button"
                onClick={() => {
                  if (showPasswordInput) setPassword("");
                  setShowPasswordInput(!showPasswordInput);
                }}
                className={`rounded-lg px-2.5 py-1 text-[11px] font-bold transition-all ${
                  showPasswordInput || password
                    ? "bg-orange-500/20 text-orange-300 border border-orange-500/40"
                    : "bg-white/5 text-white/50 hover:bg-white/10 hover:text-white"
                }`}
              >
                {showPasswordInput || password ? "Пароль включён" : "+ Добавить пароль"}
              </button>
            </div>

            {(showPasswordInput || password) && (
              <div className="relative mt-2">
                <input
                  type={showPlainPassword ? "text" : "password"}
                  placeholder="Введите пароль для защиты ссылки..."
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full rounded-lg border border-orange-500/30 bg-black/40 px-3 py-2 pr-9 text-xs text-white placeholder:text-white/30 focus:border-orange-400 focus:outline-none"
                />
                <button
                  type="button"
                  onClick={() => setShowPlainPassword(!showPlainPassword)}
                  className="absolute right-2.5 top-1/2 -translate-y-1/2 text-white/40 hover:text-white"
                >
                  {showPlainPassword ? <EyeOff size={14} /> : <Eye size={14} />}
                </button>
              </div>
            )}

            {password.trim() && (
              <div className="flex items-center gap-1.5 text-[10px] font-medium text-orange-300">
                <ShieldCheck size={12} />
                Ссылка защищена паролем
              </div>
            )}
          </div>
        </div>

        {/* QR Code Section */}
        {showQrCode && qrSvgMarkup && (
          <div className="mb-4 flex flex-col items-center justify-center rounded-xl border border-white/10 bg-black/40 p-4 animate-fade-in">
            <div
              className="rounded-lg bg-white p-2 shadow-inner"
              dangerouslySetInnerHTML={{ __html: qrSvgMarkup }}
            />
            <p className="mt-2 text-[10px] text-white/60 text-center">
              Отсканируйте камерой смартфона для открывания эксперимента
            </p>
          </div>
        )}

        {/* Main Action Buttons */}
        <div className="flex flex-col gap-2">
          {!isTooLarge && (
            <button
              type="button"
              onClick={copyLink}
              className="flex items-center justify-center gap-2 rounded-xl bg-[var(--primary)] py-2.5 text-xs font-bold text-white shadow-lg transition-all hover:bg-[var(--primary)]/85 active:scale-95"
            >
              {copied ? (
                <>
                  <Check size={16} className="text-emerald-300" />
                  Ссылка скопирована!
                </>
              ) : (
                <>
                  <Link2 size={16} />
                  Скопировать ссылку
                </>
              )}
            </button>
          )}

          <div className="grid grid-cols-2 gap-2">
            {!isTooLarge && (
              <button
                type="button"
                onClick={() => setShowQrCode(!showQrCode)}
                className={`flex items-center justify-center gap-1.5 rounded-xl border py-2 text-xs font-semibold transition-all ${
                  showQrCode
                    ? "border-cyan-500/50 bg-cyan-500/20 text-cyan-300"
                    : "border-white/10 bg-white/[0.03] text-white/80 hover:bg-white/10"
                }`}
              >
                <QrCode size={15} />
                {showQrCode ? "Скрыть QR-код" : "Показать QR-код"}
              </button>
            )}

            {typeof navigator !== "undefined" && navigator.share ? (
              <button
                type="button"
                onClick={shareNative}
                className="flex items-center justify-center gap-1.5 rounded-xl border border-white/10 bg-white/[0.03] py-2 text-xs font-semibold text-white/80 transition-all hover:bg-white/10"
              >
                <Share2 size={15} />
                Поделиться
              </button>
            ) : (
              <button
                type="button"
                onClick={onClose}
                className="flex items-center justify-center rounded-xl border border-white/10 bg-white/[0.03] py-2 text-xs font-semibold text-white/60 transition-all hover:bg-white/10 hover:text-white"
              >
                Закрыть
              </button>
            )}
          </div>
        </div>

        {/* Footer Info */}
        <div className="mt-4 border-t border-white/[.06] pt-3">
          <div className="flex justify-between text-[10px] text-white/30">
            <span>
              Приборов: {snapshot.objects.length} · Связей: {snapshot.connections.length}
            </span>
            <span>v{snapshot.version}</span>
          </div>
        </div>
      </div>
    </div>
  );
}

"use client";

import React, { useState, useRef, useEffect, useCallback } from "react";

// ─── Types ────────────────────────────────────────────────────────────────────

export interface MenuAction {
  id: string;
  label: string;
  shortcut?: string;
  disabled?: boolean;
  disabledReason?: string;
  separator?: boolean;
  icon?: React.ReactNode;
}

export interface MenuDefinition {
  id: string;
  label: string;
  items: MenuAction[];
}

interface SandboxMenuBarProps {
  canUndo: boolean;
  canRedo: boolean;
  hasSelection: boolean;
  hasMultipleItems: boolean;
  showGrid: boolean;
  showNavbar: boolean;
  isRunning: boolean;
  activeScenario: { id: string; step: number } | null;
  onAction: (actionId: string) => void;
}

// ─── Dropdown Component ───────────────────────────────────────────────────────

function DropdownMenu({
  menu,
  isOpen,
  onOpen,
  onClose,
  onAction,
}: {
  menu: MenuDefinition;
  isOpen: boolean;
  onOpen: () => void;
  onClose: () => void;
  onAction: (id: string) => void;
}) {
  const buttonRef = useRef<HTMLButtonElement>(null);
  const menuRef = useRef<HTMLDivElement>(null);
  const [focusedIndex, setFocusedIndex] = useState(-1);
  const enabledItems = menu.items.filter((item) => !item.separator);

  // Close on outside click
  useEffect(() => {
    if (!isOpen) return;
    const handleDown = (e: MouseEvent) => {
      if (
        !buttonRef.current?.contains(e.target as Node) &&
        !menuRef.current?.contains(e.target as Node)
      ) {
        onClose();
      }
    };
    document.addEventListener("mousedown", handleDown);
    return () => document.removeEventListener("mousedown", handleDown);
  }, [isOpen, onClose]);

  // Close on Escape
  useEffect(() => {
    if (!isOpen) return;
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        onClose();
        buttonRef.current?.focus();
      } else if (e.key === "ArrowDown") {
        e.preventDefault();
        setFocusedIndex((prev) => Math.min(prev + 1, enabledItems.length - 1));
      } else if (e.key === "ArrowUp") {
        e.preventDefault();
        setFocusedIndex((prev) => Math.max(prev - 1, 0));
      } else if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        const item = enabledItems[focusedIndex];
        if (item && !item.disabled) {
          onAction(item.id);
          onClose();
        }
      }
    };
    document.addEventListener("keydown", handleKey);
    return () => document.removeEventListener("keydown", handleKey);
  }, [isOpen, enabledItems, focusedIndex, onAction, onClose]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    if (!isOpen) setFocusedIndex(-1);
  }, [isOpen]);

  const handleButtonKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" || e.key === " " || e.key === "ArrowDown") {
      e.preventDefault();
      onOpen();
    }
  };

  return (
    <div className="relative">
      <button
        ref={buttonRef}
        type="button"
        aria-haspopup="menu"
        aria-expanded={isOpen}
        onMouseDown={() => (isOpen ? onClose() : onOpen())}
        onKeyDown={handleButtonKeyDown}
        className={`rounded px-2.5 py-1 text-xs font-medium transition-colors ${
          isOpen
            ? "bg-[var(--accent)] text-[var(--foreground)]"
            : "text-[var(--muted-foreground)] hover:bg-[var(--accent)] hover:text-[var(--foreground)]"
        }`}
      >
        {menu.label}
      </button>

      {isOpen && (
        <div
          ref={menuRef}
          role="menu"
          aria-label={menu.label}
          className="absolute left-0 top-full z-[200] mt-1 min-w-[200px] overflow-hidden rounded-xl border border-[var(--border)] bg-[var(--popover)] py-1 text-[var(--popover-foreground)] shadow-[0_16px_48px_rgba(0,0,0,0.24)] backdrop-blur-xl animate-fade-in-up"
          style={{ animationDuration: "100ms" }}
        >
          {menu.items.map((item, index) => {
            if (item.separator) {
              return (
                <div
                  key={`sep-${index}`}
                  role="separator"
                  className="my-1 border-t border-[var(--border)]"
                />
              );
            }
            const isFocused =
              focusedIndex === enabledItems.indexOf(item);
            return (
              <button
                key={item.id}
                type="button"
                role="menuitem"
                disabled={item.disabled}
                title={item.disabled ? item.disabledReason : undefined}
                onClick={() => {
                  if (!item.disabled) {
                    onAction(item.id);
                    onClose();
                  }
                }}
                className={`flex w-full items-center justify-between gap-4 px-4 py-1.5 text-left text-xs transition-colors ${
                  item.disabled
                    ? "cursor-not-allowed text-[var(--muted-foreground)] opacity-50"
                    : isFocused
                    ? "bg-[var(--accent)] text-[var(--foreground)]"
                    : "text-[var(--popover-foreground)] hover:bg-[var(--accent)]"
                }`}
              >
                <span className="flex items-center gap-2">
                  {item.icon && (
                    <span className="text-[var(--muted-foreground)]">{item.icon}</span>
                  )}
                  {item.label}
                </span>
                {item.shortcut && (
                  <kbd className="rounded bg-[var(--muted)] px-1.5 py-0.5 font-mono text-[10px] text-[var(--muted-foreground)]">
                    {item.shortcut}
                  </kbd>
                )}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}

// ─── Menu Bar ─────────────────────────────────────────────────────────────────

import { useTranslations } from "next-intl";
import { useTheme } from "next-themes";

export function SandboxMenuBar({
  canUndo,
  canRedo,
  hasSelection,
  hasMultipleItems,
  showGrid,
  showNavbar,
  isRunning,
  activeScenario,
  onAction,
}: SandboxMenuBarProps) {
  const [openMenu, setOpenMenu] = useState<string | null>(null);
  const t_acad = useTranslations("academy");
  const ts = useTranslations("sandbox");
  const { theme, setTheme } = useTheme();

  const menus: MenuDefinition[] = [
    {
      id: "file",
      label: ts("menu.file"),
      items: [
        { id: "new-experiment", label: ts("menu.new_exp"), shortcut: "Ctrl+N" },
        { id: "save-experiment", label: ts("menu.save_exp"), shortcut: "Ctrl+S" },
        { id: "save-copy", label: ts("menu.save_copy") },
        { id: "separator-1", label: "", separator: true },
        { id: "import-snapshot", label: ts("menu.import_snap") },
        { id: "export-snapshot", label: ts("menu.export_snap") },
        { id: "separator-2", label: "", separator: true },
        { id: "share", label: ts("menu.share") },
        { id: "separator-3", label: "", separator: true },
        {
          id: "clear-workspace",
          label: ts("menu.clear_workspace"),
          disabled: false,
        },
      ],
    },
    {
      id: "edit",
      label: ts("menu.edit"),
      items: [
        {
          id: "undo",
          label: ts("menu.undo"),
          shortcut: "Ctrl+Z",
          disabled: !canUndo,
          disabledReason: ts("menu.no_undo"),
        },
        {
          id: "redo",
          label: ts("menu.redo"),
          shortcut: "Ctrl+Shift+Z",
          disabled: !canRedo,
          disabledReason: ts("menu.no_redo"),
        },
        { id: "separator-edit-1", label: "", separator: true },
        {
          id: "copy",
          label: ts("menu.copy"),
          shortcut: "Ctrl+C",
          disabled: !hasSelection,
          disabledReason: "Выберите объект",
        },
        {
          id: "duplicate",
          label: ts("menu.copy"),
          shortcut: "Ctrl+D",
          disabled: !hasSelection,
          disabledReason: "Выберите объект",
        },
        {
          id: "delete",
          label: ts("menu.delete_selected"),
          shortcut: "Del",
          disabled: !hasSelection,
          disabledReason: "Выберите объект",
        },
        { id: "separator-edit-2", label: "", separator: true },
        { id: "select-all", label: ts("menu.select_all"), shortcut: "Ctrl+A" },
        {
          id: "deselect",
          label: ts("menu.deselect"),
          shortcut: "Esc",
          disabled: !hasSelection,
        },
      ],
    },
    {
      id: "academy",
      label: t_acad("menuLabel") || "Учёба",
      items: [
        { id: "jasscience-os", label: t_acad("title"), shortcut: "B" },
        { id: "separator-acad-1", label: "", separator: true },
        { id: "level-1", label: t_acad("levels.l1.title") },
        { id: "level-2", label: t_acad("levels.l2.title") },
        { id: "level-3", label: t_acad("levels.l3.title") },
        { id: "level-4", label: t_acad("levels.l4.title") },
      ],
    },
    {
      id: "view",
      label: ts("menu.view"),
      items: [
        {
          id: "toggle-grid",
          label: ts("menu.show_grid"),
          shortcut: "G",
        },
        { id: "toggle-snap", label: ts("menu.snap_grid") },
        { id: "separator-view-1", label: "", separator: true },
        { id: "toggle-theme", label: ts("menu.theme") },
        { id: "separator-view-2", label: "", separator: true },
        { id: "zoom-in", label: ts("menu.zoom_in"), shortcut: "+" },
        { id: "zoom-out", label: ts("menu.zoom_out"), shortcut: "-" },
        { id: "zoom-100", label: "100%", shortcut: "Ctrl+0" },
        { id: "zoom-fit", label: "Подогнать сцену", shortcut: "F" },
        { id: "center-scene", label: ts("menu.center_scene"), shortcut: "Shift+F" },
        { id: "separator-view-2", label: "", separator: true },
        {
          id: "toggle-library",
          label: ts("menu.toggle_library"),
          shortcut: "Ctrl+[",
        },
        {
          id: "toggle-inspector",
          label: ts("menu.toggle_inspector"),
          shortcut: "Ctrl+]",
        },
        {
          id: "toggle-navbar",
          label: ts(showNavbar ? "menu.hide_navbar" : "menu.show_navbar"),
        },
        { id: "separator-view-3", label: "", separator: true },
        { id: "fullscreen", label: ts("menu.toggle_fullscreen"), shortcut: "F11" },
      ],
    },
    {
      id: "arrange",
      label: ts("menu.arrange"),
      items: [
        {
          id: "align-left",
          label: ts("menu.align_left"),
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        {
          id: "align-center-h",
          label: ts("menu.align_center_h"),
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        {
          id: "align-right",
          label: ts("menu.align_right"),
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        { id: "separator-arr-1", label: "", separator: true },
        {
          id: "align-top",
          label: ts("menu.align_top"),
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        {
          id: "align-middle-v",
          label: "По середине (верт.)",
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        {
          id: "align-bottom",
          label: ts("menu.align_bottom"),
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        { id: "separator-arr-2", label: "", separator: true },
        {
          id: "distribute-h",
          label: ts("menu.distribute_h"),
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        {
          id: "distribute-v",
          label: ts("menu.distribute_v"),
          disabled: !hasMultipleItems,
          disabledReason: "Выберите несколько объектов",
        },
        { id: "separator-arr-3", label: "", separator: true },
        {
          id: "group",
          label: "Group",
          disabled: true,
          disabledReason: "Будет доступно позже",
        },
        {
          id: "ungroup",
          label: "Ungroup",
          disabled: true,
          disabledReason: "Будет доступно позже",
        },
      ],
    },
    {
      id: "more",
      label: ts("menu.extra"),
      items: [
        {
          id: "free-mode",
          label:
            activeScenario === null
              ? "✓ Свободный режим"
              : "Перейти в свободный режим",
        },
        {
          id: "scenarios",
          label: "Сценарии…",
        },
        { id: "separator-more-1", label: "", separator: true },
        {
          id: "safety-check",
          label: "Проверка безопасности",
          disabled: isRunning,
          disabledReason: "Остановите симуляцию",
        },
        { id: "schema-check", label: "Schema check" },
        { id: "separator-more-2", label: "", separator: true },
        { id: "clear-states", label: "Очистить временные состояния" },
        ...(process.env.NODE_ENV === "development"
          ? [
              { id: "separator-more-3", label: "", separator: true } as MenuAction,
              {
                id: "debug-mode",
                label: "Debug mode",
              } as MenuAction,
            ]
          : []),
      ],
    },
  ];

  const handleClose = useCallback(() => setOpenMenu(null), []);

  const handleAction = useCallback(
    (actionId: string) => {
      if (actionId === "toggle-theme") {
        setTheme(theme === "dark" ? "light" : "dark");
      } else {
        onAction(actionId);
      }
      setOpenMenu(null);
    },
    [onAction, theme, setTheme]
  );

  return (
    <nav
      aria-label="Главное меню лаборатории"
      className="sandbox-menu-bar hidden items-center gap-0.5 md:flex"
    >
      {menus.map((menu) => (
        <DropdownMenu
          key={menu.id}
          menu={menu}
          isOpen={openMenu === menu.id}
          onOpen={() => setOpenMenu(menu.id)}
          onClose={handleClose}
          onAction={handleAction}
        />
      ))}
    </nav>
  );
}

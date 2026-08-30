"use client";

import { usePathname } from "next/navigation";
import { useEffect } from "react";
import { ThemeProvider as NextThemesProvider, useTheme } from "next-themes";

const ALLOWED_THEME_PATHS = ["/dashboard", "/workspace/sandbox", "/admin"];
const THEME_PREFERENCE_KEY = "chemistry-theme-preference";

function RouteThemeController() {
  const pathname = usePathname();
  const { theme, setTheme } = useTheme();

  useEffect(() => {
    const localizedPath = pathname.replace(/^\/[^/]+/, "") || "/";
    const canChooseTheme = ALLOWED_THEME_PATHS.some((path) => localizedPath === path || localizedPath.startsWith(`${path}/`));
    const savedPreference = window.localStorage.getItem(THEME_PREFERENCE_KEY);

    if (canChooseTheme) {
      // Restore the user's choice when returning from a page that is always dark.
      if ((savedPreference === "light" || savedPreference === "dark") && theme !== savedPreference) {
        setTheme(savedPreference);
      } else if (!savedPreference && theme && theme !== "system") {
        window.localStorage.setItem(THEME_PREFERENCE_KEY, theme);
      }
      return;
    }

    // Keep the preference for Dashboard/Sandbox, but force every other route dark.
    if (theme === "light") window.localStorage.setItem(THEME_PREFERENCE_KEY, "light");
    if (theme !== "dark") setTheme("dark");
  }, [pathname, setTheme, theme]);

  return null;
}

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  return (
    <NextThemesProvider attribute="class" defaultTheme="dark" enableSystem>
      <RouteThemeController />
      {children}
    </NextThemesProvider>
  );
}

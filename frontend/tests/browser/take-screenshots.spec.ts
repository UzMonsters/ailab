import { test } from '@playwright/test';

test('Capture Screenshots for UI Review', async ({ page }) => {
  // Capture Equipment Editor
  await page.goto('/en/admin/equipment/new');
  await page.waitForTimeout(2000); // wait for load
  await page.screenshot({ path: 'test-results/screenshots/equipment.png', fullPage: true });

  // Capture Scenario Editor
  await page.goto('/en/admin/scenarios/new');
  await page.waitForTimeout(2000);
  await page.screenshot({ path: 'test-results/screenshots/scenario.png', fullPage: true });

  // Capture Book Studio
  await page.goto('/en/admin/book');
  await page.waitForTimeout(2000);
  await page.screenshot({ path: 'test-results/screenshots/book.png', fullPage: true });

  // Capture Level Editor
  await page.goto('/en/admin/learning/levels/new');
  await page.waitForTimeout(2000);
  await page.screenshot({ path: 'test-results/screenshots/level.png', fullPage: true });
});

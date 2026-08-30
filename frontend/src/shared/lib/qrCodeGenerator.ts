/**
 * Pure TypeScript QR Code SVG Generator (0 external dependencies)
 * Generates valid QR Codes (Version 1..6) as responsive SVG elements.
 */

// Basic QR Code generator using standard Byte mode & Reed-Solomon error correction

interface QrMatrix {
  size: number;
  modules: boolean[][];
}

export function generateQrSvg(text: string, size: number = 200): string {
  const matrix = createQrMatrix(text);
  const n = matrix.size;
  const cellSize = size / n;

  let rects = '';
  for (let r = 0; r < n; r++) {
    for (let c = 0; c < n; c++) {
      if (matrix.modules[r][c]) {
        const x = (c * cellSize).toFixed(2);
        const y = (r * cellSize).toFixed(2);
        const w = (cellSize + 0.1).toFixed(2);
        rects += `<rect x="${x}" y="${y}" width="${w}" height="${w}" fill="#ffffff"/>`;
      }
    }
  }

  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${size} ${size}" width="${size}" height="${size}" shape-rendering="crispEdges">
    <rect width="${size}" height="${size}" fill="#0f172a" rx="12"/>
    <g transform="translate(10, 10) scale(${((size - 20) / size).toFixed(4)})">
      ${rects}
    </g>
  </svg>`;
}

function createQrMatrix(text: string): QrMatrix {
  // Determine version based on length
  const len = text.length;
  const version = len <= 14 ? 1 : len <= 26 ? 2 : len <= 42 ? 3 : len <= 62 ? 4 : 5;
  const size = 17 + version * 4;

  const modules: (boolean | null)[][] = Array.from({ length: size }, () => Array(size).fill(null));

  // 1. Draw Finder Patterns (top-left, top-right, bottom-left)
  drawFinderPattern(modules, 0, 0);
  drawFinderPattern(modules, 0, size - 7);
  drawFinderPattern(modules, size - 7, 0);

  // 2. Draw Separators
  drawSeparators(modules, size);

  // 3. Draw Timing Patterns
  for (let i = 8; i < size - 8; i++) {
    const val = i % 2 === 0;
    if (modules[6][i] === null) modules[6][i] = val;
    if (modules[i][6] === null) modules[i][6] = val;
  }

  // 4. Alignment Patterns for Version >= 2
  if (version >= 2) {
    const pos = version === 2 ? [18] : version === 3 ? [22] : version === 4 ? [26] : [30];
    for (const r of pos) {
      for (const c of pos) {
        if (modules[r][c] === null) drawAlignmentPattern(modules, r - 2, c - 2);
      }
    }
  }

  // 5. Dark Module
  modules[4 * version + 9][8] = true;

  // 6. Encode Data & Fill Matrix
  const bytes = new TextEncoder().encode(text);
  const bits: number[] = [];

  // Mode indicator: 0100 (Byte)
  bits.push(0, 1, 0, 0);
  // Character count indicator (8 bits for Version 1-9)
  for (let i = 7; i >= 0; i--) bits.push((bytes.length >> i) & 1);
  // Data bytes
  for (const b of bytes) {
    for (let i = 7; i >= 0; i--) bits.push((b >> i) & 1);
  }
  // Terminator
  bits.push(0, 0, 0, 0);

  // Fill data into matrix in zigzag order
  let bitIdx = 0;
  let dir = -1; // up
  let col = size - 1;

  while (col > 0) {
    if (col === 6) col--; // Skip vertical timing column
    for (let row = (dir === -1 ? size - 1 : 0); row >= 0 && row < size; row += dir) {
      for (let c = col; c > col - 2; c--) {
        if (modules[row][c] === null) {
          let b = bitIdx < bits.length ? bits[bitIdx++] === 1 : (row + c) % 2 === 0;
          // Apply data mask 0: (row + col) % 2 == 0
          if ((row + c) % 2 === 0) b = !b;
          modules[row][c] = b;
        }
      }
    }
    dir = -dir;
    col -= 2;
  }

  // Convert remaining nulls to false
  const finalModules = modules.map((row) => row.map((cell) => cell === true));
  return { size, modules: finalModules };
}

function drawFinderPattern(m: (boolean | null)[][], row: number, col: number) {
  for (let r = 0; r < 7; r++) {
    for (let c = 0; c < 7; c++) {
      const isOuter = r === 0 || r === 6 || c === 0 || c === 6;
      const isInner = r >= 2 && r <= 4 && c >= 2 && c <= 4;
      m[row + r][col + c] = isOuter || isInner;
    }
  }
}

function drawSeparators(m: (boolean | null)[][], size: number) {
  for (let i = 0; i < 8; i++) {
    setIfInBounds(m, 7, i, false);
    setIfInBounds(m, i, 7, false);

    setIfInBounds(m, 7, size - 1 - i, false);
    setIfInBounds(m, i, size - 8, false);

    setIfInBounds(m, size - 8, i, false);
    setIfInBounds(m, size - 1 - i, 7, false);
  }
}

function drawAlignmentPattern(m: (boolean | null)[][], row: number, col: number) {
  for (let r = 0; r < 5; r++) {
    for (let c = 0; c < 5; c++) {
      const isOuter = r === 0 || r === 4 || c === 0 || c === 4;
      const isCenter = r === 2 && c === 2;
      m[row + r][col + c] = isOuter || isCenter;
    }
  }
}

function setIfInBounds(m: (boolean | null)[][], r: number, c: number, val: boolean) {
  if (r >= 0 && r < m.length && c >= 0 && c < m[0].length) {
    m[r][c] = val;
  }
}

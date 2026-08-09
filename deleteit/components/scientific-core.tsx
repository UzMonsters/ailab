'use client'

import * as React from 'react'

type RingConfig = {
  rx: number
  ry: number
  rotate: number
  dur: number
  dir: 1 | -1
  front?: boolean
  bright?: boolean
}

type Point = { x: number; y: number; r: number; color: string }

function clamp01(value: number) {
  return Math.min(1, Math.max(0, value))
}

function hexToRgb(hex: string) {
  const normalized = hex.replace('#', '')
  const value = Number.parseInt(normalized.length === 3 ? normalized.split('').map((char) => char + char).join('') : normalized, 16)
  return { r: (value >> 16) & 255, g: (value >> 8) & 255, b: value & 255 }
}

function mix(a: string, b: string, amount: number) {
  const colorA = hexToRgb(a)
  const colorB = hexToRgb(b)
  const channel = (start: number, end: number) => Math.round(start + (end - start) * clamp01(amount)).toString(16).padStart(2, '0')
  return `#${channel(colorA.r, colorB.r)}${channel(colorA.g, colorB.g)}${channel(colorA.b, colorB.b)}`
}

export default function ScientificCore({ size = 620, accentColor = '#7c3aed' }: { size?: number; accentColor?: string }) {
  const svgRef = React.useRef<SVGSVGElement>(null)
  const [hover, setHover] = React.useState({ x: 0, y: 0 })
  const [waves, setWaves] = React.useState<number[]>([])
  const center = size / 2
  const palette = React.useMemo(() => [
    '#ffffff', mix('#ffffff', accentColor, .62), accentColor, mix(accentColor, '#0b0620', .55), mix(accentColor, '#050311', .82),
  ], [accentColor])
  const colorAt = React.useCallback((value: number) => {
    const scaled = clamp01(value) * (palette.length - 1)
    const index = Math.min(palette.length - 2, Math.floor(scaled))
    return mix(palette[index], palette[index + 1], scaled - index)
  }, [palette])
  const rings = React.useMemo<RingConfig[]>(() => [
    { rx: size * .42, ry: size * .15, rotate: 6, dur: 46, dir: 1 },
    { rx: size * .36, ry: size * .27, rotate: 52, dur: 60, dir: -1, front: true },
    { rx: size * .30, ry: size * .34, rotate: 100, dur: 38, dir: 1, bright: true },
    { rx: size * .24, ry: size * .41, rotate: 145, dur: 70, dir: -1, front: true },
    { rx: size * .20, ry: size * .20, rotate: 172, dur: 54, dir: 1 },
    { rx: size * .46, ry: size * .30, rotate: 208, dur: 64, dir: -1, front: true, bright: true },
  ], [size])
  const atoms = React.useMemo(() => {
    const points: Point[] = []
    rings.slice(0, 4).forEach((ring, ringIndex) => {
      const count = 9 + ringIndex * 3
      const rotation = ring.rotate * Math.PI / 180
      for (let index = 0; index < count; index += 1) {
        const angle = index / count * Math.PI * 2 + ringIndex * .33
        const xLocal = Math.cos(angle) * ring.rx
        const yLocal = Math.sin(angle) * ring.ry * (1 + .05 * Math.sin(angle * 4))
        points.push({
          x: center + xLocal * Math.cos(rotation) - yLocal * Math.sin(rotation),
          y: center + xLocal * Math.sin(rotation) + yLocal * Math.cos(rotation),
          r: size * (.0036 + (ringIndex % 3) * .0011),
          color: colorAt(ringIndex / 4 * .75 + index % 3 * .03),
        })
      }
    })
    return points
  }, [center, colorAt, rings, size])
  const links = React.useMemo(() => atoms.flatMap((_, index) => {
    const result: Array<{ from: number; to: number; phase: number }> = []
    if (index + 1 < atoms.length) result.push({ from: index, to: index + 1, phase: index % 5 })
    if (index + 7 < atoms.length && index % 3 === 0) result.push({ from: index, to: index + 7, phase: (index + 2) % 5 })
    return result
  }), [atoms])
  const particles = React.useMemo(() => new Array(110).fill(null).map((_, index) => {
    const angle = index / 110 * Math.PI * 2 * 3.1 + index * .7
    const radius = size * (.16 + ((index * 37) % 100) / 100 * .42)
    return { x: center + Math.cos(angle) * radius, y: center + Math.sin(angle) * radius, r: size * (.0016 + index % 5 * .001), delay: index % 23 * .35, duration: 4 + index % 9 * .8, color: index % 6 === 0 ? '#fff' : colorAt(.15 + index % 7 * .1) }
  }), [center, colorAt, size])
  const pathForRing = (rx: number, ry: number) => `M ${center + rx} ${center} A ${rx} ${ry} 0 1 0 ${center - rx} ${center} A ${rx} ${ry} 0 1 0 ${center + rx} ${center}`
  const handlePointerMove = (event: React.PointerEvent<SVGSVGElement>) => {
    const rect = svgRef.current?.getBoundingClientRect()
    if (!rect) return
    setHover({ x: ((event.clientX - rect.left) / rect.width - .5), y: ((event.clientY - rect.top) / rect.height - .5) })
  }
  const handleClick = () => {
    const id = Date.now()
    setWaves((current) => [...current, id])
    window.setTimeout(() => setWaves((current) => current.filter((wave) => wave !== id)), 1500)
  }
  const renderRing = (ring: RingConfig, index: number) => {
    const path = pathForRing(ring.rx, ring.ry)
    return <g key={`${ring.rotate}-${index}`} transform={`rotate(${ring.rotate} ${center} ${center})`} className={ring.bright ? 'scientific-ring scientific-ring-bright' : 'scientific-ring'}>
      <ellipse cx={center} cy={center} rx={ring.rx} ry={ring.ry} />
      <circle r={size * .0044} fill="#fff"><animateMotion dur={`${ring.dur * .22}s`} repeatCount="indefinite" path={path} /></circle>
      <circle r={size * .0028} fill={accentColor} opacity=".55"><animateMotion dur={`${ring.dur * .22}s`} begin="-.18s" repeatCount="indefinite" path={path} /></circle>
    </g>
  }
  return <div className="scientific-core-model" aria-hidden="true">
    <svg ref={svgRef} viewBox={`0 0 ${size} ${size}`} width="100%" height="100%" onPointerMove={handlePointerMove} onPointerLeave={() => setHover({ x: 0, y: 0 })} onClick={handleClick}>
      <defs>
        <radialGradient id="scientific-nucleus-outer"><stop stopColor={accentColor} stopOpacity=".58" /><stop offset=".45" stopColor={accentColor} stopOpacity=".25" /><stop offset="1" stopColor={accentColor} stopOpacity="0" /></radialGradient>
        <radialGradient id="scientific-nucleus-core"><stop stopColor="#fff" /><stop offset=".35" stopColor={accentColor} stopOpacity=".95" /><stop offset="1" stopColor={accentColor} stopOpacity=".18" /></radialGradient>
        <filter id="scientific-blur"><feGaussianBlur stdDeviation={size * .008} /></filter>
      </defs>
      <g className="scientific-particles">{particles.map((particle, index) => <circle key={index} cx={particle.x} cy={particle.y} r={particle.r} fill={particle.color}><animate attributeName="opacity" values="0;.75;0" dur={`${particle.duration}s`} begin={`${particle.delay}s`} repeatCount="indefinite" /></circle>)}</g>
      <g className="scientific-rings scientific-rings-back">{rings.filter((ring) => !ring.front).map(renderRing)}</g>
      <g className="scientific-network">{links.map((link, index) => { const from = atoms[link.from]; const to = atoms[link.to]; return <line key={index} x1={from.x} y1={from.y} x2={to.x} y2={to.y} stroke={index % 4 === 0 ? '#fff' : accentColor}><animate attributeName="stroke-opacity" values=".12;.38;.12" dur={`${3.5 + link.phase * .6}s`} begin={`${link.phase * .4}s`} repeatCount="indefinite" /></line> })}</g>
      <g className="scientific-pulses">{links.slice(0, 26).map((link, index) => { const from = atoms[link.from]; const to = atoms[link.to]; return <circle key={index} r={size * .0038} fill={accentColor}><animateMotion dur={`${2.6 + index % 5 * .6}s`} begin={`${index * .18}s`} repeatCount="indefinite" path={`M ${from.x} ${from.y} L ${to.x} ${to.y}`} /><animate attributeName="opacity" values="0;1;0" dur={`${2.6 + index % 5 * .6}s`} repeatCount="indefinite" /></circle> })}</g>
      <g className="scientific-atoms">{atoms.map((atom, index) => <g key={index}><circle cx={atom.x} cy={atom.y} r={atom.r} fill={atom.color} /><circle cx={atom.x} cy={atom.y} r={atom.r * .9} fill={atom.color} opacity=".3"><animate attributeName="r" values={`${atom.r * .8};${atom.r * 2.4};${atom.r * .8}`} dur={`${3 + index % 6 * .5}s`} repeatCount="indefinite" /></circle></g>)}</g>
      <g className="scientific-nucleus" style={{ transform: `translate(${hover.x * size * .012}px, ${hover.y * size * .012}px)` }}><circle cx={center} cy={center} r={size * .3} fill="url(#scientific-nucleus-outer)" filter="url(#scientific-blur)" /><circle cx={center} cy={center} r={size * .14} fill="url(#scientific-nucleus-core)" filter="url(#scientific-blur)"><animate attributeName="r" values={`${size * .12};${size * .155};${size * .12}`} dur="7s" repeatCount="indefinite" /></circle><circle cx={center} cy={center} r={size * .038} fill="url(#scientific-nucleus-core)"><animate attributeName="r" values={`${size * .034};${size * .044};${size * .034}`} dur="7s" repeatCount="indefinite" /></circle>{waves.map((wave) => <circle key={wave} cx={center} cy={center} r={size * .04} fill="none" stroke="#fff" strokeWidth={size * .002}><animate attributeName="r" values={`${size * .04};${size * .5}`} dur="1.4s" fill="freeze" /><animate attributeName="opacity" values=".9;0" dur="1.4s" fill="freeze" /></circle>)}</g>
      <g className="scientific-rings scientific-rings-front">{rings.filter((ring) => ring.front).map(renderRing)}</g>
    </svg>
  </div>
}

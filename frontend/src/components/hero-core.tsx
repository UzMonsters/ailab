'use client'

import * as React from 'react'

export default function HeroCore() {
  const stageRef = React.useRef<HTMLDivElement>(null)
  const [pointer, setPointer] = React.useState({ x: 0, y: 0, active: false })

  const handlePointerMove = (event: React.PointerEvent<HTMLDivElement>) => {
    const rect = stageRef.current?.getBoundingClientRect()
    if (!rect) return
    const x = event.clientX - (rect.left + rect.width / 2)
    const y = event.clientY - (rect.top + rect.height / 2)
    setPointer({ x, y, active: Math.hypot(x, y) < 260 })
  }

  const pull = pointer.active ? Math.max(0, 1 - Math.hypot(pointer.x, pointer.y) / 260) : 0

  return <div ref={stageRef} className="hero-core" aria-hidden="true" onPointerMove={handlePointerMove} onPointerLeave={() => setPointer({ x: 0, y: 0, active: false })}>
    <div className="hc-nebula-layer"><img className="hc-nebula" src="/background_nebula.png" alt="" /></div>

    <div className="hc-layer hc-grid-layer"><img className="hc-img hc-grid" src="/scientific_grid.png" alt="" /></div>

    <div className="hc-layer hc-ripple-layer">
      <img className="hc-img hc-ripple hc-ripple-a" src="/ripple_waves.png" alt="" />
      <img className="hc-img hc-ripple hc-ripple-b" src="/ripple_waves.png" alt="" />
      <img className="hc-img hc-ripple hc-ripple-c" src="/ripple_waves.png" alt="" />
    </div>

    <div className="hc-layer hc-network-layer" style={{ transform: `translate(${pointer.x * .012}px, ${pointer.y * .012}px)` }}>
      <div className="hc-network-rot"><img className="hc-img hc-network" src="/molecular_network.png" alt="" /></div>
    </div>

    <div className="hc-layer hc-rings-layer" style={{ transform: `perspective(900px) rotateX(${-pointer.y * .05}deg) rotateY(${pointer.x * .05}deg)` }}>
      <img className="hc-img hc-ring hc-ring-1" src="/orbital_ring.png" alt="" />
      <img className="hc-img hc-ring hc-ring-2" src="/orbital_ring.png" alt="" />
      <img className="hc-img hc-ring hc-ring-3" src="/orbital_ring.png" alt="" />
      <img className="hc-img hc-ring hc-ring-4" src="/orbital_ring.png" alt="" />
    </div>

    <div className="hc-orbit hc-orbit-1" />
    <div className="hc-orbit hc-orbit-2" />

    <div className="hc-atom-cluster hc-atoms-a" style={{ transform: `translate(${pointer.x * .05}px, ${pointer.y * .05}px)` }}>
      <img className="hc-img hc-atoms" src="/floating_atoms.png" alt="" />
    </div>
    <div className="hc-atom-cluster hc-atoms-b" style={{ transform: `translate(${pointer.x * .02}px, ${pointer.y * .02}px)` }}>
      <img className="hc-img hc-atoms hc-atoms-far" src="/floating_atoms.png" alt="" />
    </div>

    <div className="hc-layer hc-pulses-layer" style={{ transform: `translate(${pointer.x * .03}px, ${pointer.y * .03}px)`, filter: `brightness(${1 + pull * .3})` }}>
      <img className="hc-img hc-pulses" src="/data_pulses.png" alt="" />
    </div>

    <div className="hc-layer hc-sparks-layer">
      <img className="hc-img hc-spark hc-spark-a" src="/energy_sparks.png" alt="" />
      <img className="hc-img hc-spark hc-spark-b" src="/energy_sparks.png" alt="" />
    </div>

    <div className="hc-layer hc-core-layer" style={{ transform: `translate(${pointer.x * .03}px, ${pointer.y * .03}px)` }}>
      <img className="hc-img hc-core" src="/core.png" alt="" />
    </div>

    <div className="hc-layer hc-glow-layer" style={{ transform: `translate(${pointer.x * .02}px, ${pointer.y * .02}px)`, filter: `brightness(${1 + pull * .2})` }}>
      <div className="hc-glow" />
    </div>
  </div>
}

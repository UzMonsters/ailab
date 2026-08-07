# Acid-Base Reference Data Sources & Provenance

## Dataset Identification
- **Version**: `1.0.0`
- **File**: `acid-base-reference-v1.json`
- **Source Identifier**: `IUPAC/CRC-HANDBOOK`
- **Citation**: CRC Handbook of Chemistry and Physics, 104th Edition (2023-2024), Section 8: Dissociation Constants of Acids and Bases; IUPAC Critical Evaluation of Equilibrium Constants in Aqueous Systems.
- **License / Terms**: CRC Handbook Citation / IUPAC Recommended Standards (Internal Calculation for Derived pK Values).
- **Evidence Status**: `PEER_REVIEWED_EXPERIMENTAL`

## Species Coverage (19 Core Species)
1. `SPEC-H2O` (Water, $H_2O$, solvent, AMPHIPROTIC, WEAK_ELECTROLYTE)
2. `SPEC-H3O-PLUS` (Hydronium, $H_3O^+$, ACID, STRONG_ELECTROLYTE)
3. `SPEC-OH-MINUS` (Hydroxide, $OH^-$, BASE, STRONG_ELECTROLYTE)
4. `SPEC-H-PLUS` (Hydrogen Ion, $H^+$, ACID, STRONG_ELECTROLYTE)
5. `SPEC-NA-PLUS` (Sodium Ion, $Na^+$, NEUTRAL, STRONG_ELECTROLYTE)
6. `SPEC-CL-MINUS` (Chloride Ion, $Cl^-$, NEUTRAL, STRONG_ELECTROLYTE)
7. `SPEC-NH4-PLUS` (Ammonium, $NH_4^+$, ACID, WEAK_ELECTROLYTE)
8. `SPEC-NH3` (Ammonia, $NH_3$, BASE, WEAK_ELECTROLYTE)
9. `SPEC-HCL` (Hydrochloric Acid, $HCl$, ACID, STRONG_ELECTROLYTE)
10. `SPEC-HNO3` (Nitric Acid, $HNO_3$, ACID, STRONG_ELECTROLYTE)
11. `SPEC-H2SO4` (Sulfuric Acid, $H_2SO_4$, ACID, STRONG_ELECTROLYTE)
12. `SPEC-NAOH` (Sodium Hydroxide, $NaOH$, BASE, STRONG_ELECTROLYTE)
13. `SPEC-HSO4-MINUS` (Hydrogen Sulfate, $HSO_4^-$, AMPHIPROTIC, WEAK_ELECTROLYTE)
14. `SPEC-SO4-2MINUS` (Sulfate, $SO_4^{2-}$, BASE, WEAK_ELECTROLYTE)
15. `SPEC-CH3COOH` (Acetic Acid, $CH_3COOH$, ACID, WEAK_ELECTROLYTE)
16. `SPEC-CH3COO-MINUS` (Acetate, $CH_3COO^-$, BASE, WEAK_ELECTROLYTE)
17. `SPEC-H2CO3` (Carbonic Acid, $H_2CO_3$, ACID, WEAK_ELECTROLYTE)
18. `SPEC-HCO3-MINUS` (Bicarbonate, $HCO_3^-$, AMPHIPROTIC, WEAK_ELECTROLYTE)
19. `SPEC-CO3-2MINUS` (Carbonate, $CO_3^{2-}$, BASE, WEAK_ELECTROLYTE)

## Primary Constants Summary ($25^\circ\text{C}$ in Water)
- **Water Autoionization**: $K_w = 1.0 \times 10^{-14}$ ($pK_w = 14.0000$) [CRC / IUPAC Standard].
- **Acetic Acid**: $K_{a1} = 1.75 \times 10^{-5}$ ($pK_a = 4.7567$) [CRC / IUPAC Standard].
- **Acetate**: $K_{b1} = 5.71 \times 10^{-10}$ ($pK_b = 9.2433$) [Derived via $K_w / K_a$ internal calculation].
- **Carbonic Acid**: $K_{a1} = 4.45 \times 10^{-7}$ ($pK_{a1} = 6.3516$) [CRC / IUPAC Standard].
- **Bicarbonate**: $K_{a2} = 4.69 \times 10^{-11}$ ($pK_{a2} = 10.3288$), $K_{b1} = 2.25 \times 10^{-8}$ ($pK_{b1} = 7.6484$).
- **Carbonate**: $K_{b1} = 2.13 \times 10^{-4}$ ($pK_{b1} = 3.6712$).
- **Ammonium**: $K_a = 5.69 \times 10^{-10}$ ($pK_a = 9.2449$) [Derived via $K_w / K_b$ internal calculation].
- **Ammonia**: $K_b = 1.76 \times 10^{-5}$ ($pK_b = 4.7545$) [CRC / IUPAC Standard].
- **Hydrogen Sulfate**: $K_{a2} = 1.02 \times 10^{-2}$ ($pK_{a2} = 1.9914$).
- **Strong Electrolytes**: $HCl, HNO_3, H_2SO_4, NaOH$ marked `dissociationBehavior = STRONG_ELECTROLYTE`. No fake equilibrium constants.

package com.ailab.chemistry.element;

import com.ailab.chemistry.domain.element.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Data generator test. Writes generated files to target/generated-periodic-table/ ONLY.
 * Does NOT modify tracked source files during mvn verify.
 * Generation is deterministic: same input produces same byte-for-byte output.
 */
public class GenerateElementDataTest {

    // Full 118-element dataset (v1.1.0)
    // Format: atomicNumber|symbol|name|latinName|atomicMassValue|atomicMassKind|lowerBound|upperBound|period|group|block|category|series|standardState|radioactivityStatus|electronConfig|configStatus
    public static final String[] ELEMENT_DATA = {
        "1|H|Hydrogen||1.008|INTERVAL_STANDARD_ATOMIC_WEIGHT|1.00784|1.00811|1|1|S|REACTIVE_NONMETAL|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|1s1|EVALUATED",
        "2|He|Helium||4.0026|STANDARD_ATOMIC_WEIGHT|||1|18|S|NOBLE_GAS|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|1s2|EVALUATED",
        "3|Li|Lithium||6.94|INTERVAL_STANDARD_ATOMIC_WEIGHT|6.938|6.997|2|1|S|ALKALI_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[He] 2s1|EVALUATED",
        "4|Be|Beryllium||9.0122|STANDARD_ATOMIC_WEIGHT|||2|2|S|ALKALINE_EARTH_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[He] 2s2|EVALUATED",
        "5|B|Boron||10.81|INTERVAL_STANDARD_ATOMIC_WEIGHT|10.806|10.821|2|13|P|METALLOID|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[He] 2s2 2p1|EVALUATED",
        "6|C|Carbon||12.011|INTERVAL_STANDARD_ATOMIC_WEIGHT|12.0096|12.0116|2|14|P|REACTIVE_NONMETAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[He] 2s2 2p2|EVALUATED",
        "7|N|Nitrogen||14.007|INTERVAL_STANDARD_ATOMIC_WEIGHT|14.00643|14.00728|2|15|P|REACTIVE_NONMETAL|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[He] 2s2 2p3|EVALUATED",
        "8|O|Oxygen||15.999|INTERVAL_STANDARD_ATOMIC_WEIGHT|15.99491|15.99977|2|16|P|REACTIVE_NONMETAL|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[He] 2s2 2p4|EVALUATED",
        "9|F|Fluorine||18.998|STANDARD_ATOMIC_WEIGHT|||2|17|P|REACTIVE_NONMETAL|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[He] 2s2 2p5|EVALUATED",
        "10|Ne|Neon||20.180|STANDARD_ATOMIC_WEIGHT|||2|18|P|NOBLE_GAS|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[He] 2s2 2p6|EVALUATED",
        "11|Na|Sodium|Natrium|22.990|STANDARD_ATOMIC_WEIGHT|||3|1|S|ALKALI_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ne] 3s1|EVALUATED",
        "12|Mg|Magnesium||24.305|INTERVAL_STANDARD_ATOMIC_WEIGHT|24.304|24.307|3|2|S|ALKALINE_EARTH_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ne] 3s2|EVALUATED",
        "13|Al|Aluminium||26.982|STANDARD_ATOMIC_WEIGHT|||3|13|P|POST_TRANSITION_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ne] 3s2 3p1|EVALUATED",
        "14|Si|Silicon||28.085|INTERVAL_STANDARD_ATOMIC_WEIGHT|28.084|28.086|3|14|P|METALLOID|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ne] 3s2 3p2|EVALUATED",
        "15|P|Phosphorus||30.974|STANDARD_ATOMIC_WEIGHT|||3|15|P|REACTIVE_NONMETAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ne] 3s2 3p3|EVALUATED",
        "16|S|Sulfur||32.06|INTERVAL_STANDARD_ATOMIC_WEIGHT|32.059|32.076|3|16|P|REACTIVE_NONMETAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ne] 3s2 3p4|EVALUATED",
        "17|Cl|Chlorine||35.45|INTERVAL_STANDARD_ATOMIC_WEIGHT|35.446|35.457|3|17|P|REACTIVE_NONMETAL|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[Ne] 3s2 3p5|EVALUATED",
        "18|Ar|Argon||39.948|INTERVAL_STANDARD_ATOMIC_WEIGHT|39.792|39.963|3|18|P|NOBLE_GAS|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[Ne] 3s2 3p6|EVALUATED",
        "19|K|Potassium|Kalium|39.098|STANDARD_ATOMIC_WEIGHT|||4|1|S|ALKALI_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ar] 4s1|EVALUATED",
        "20|Ca|Calcium||40.078|STANDARD_ATOMIC_WEIGHT|||4|2|S|ALKALINE_EARTH_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ar] 4s2|EVALUATED",
        "21|Sc|Scandium||44.956|STANDARD_ATOMIC_WEIGHT|||4|3|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d1 4s2|EVALUATED",
        "22|Ti|Titanium||47.867|STANDARD_ATOMIC_WEIGHT|||4|4|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d2 4s2|EVALUATED",
        "23|V|Vanadium||50.942|STANDARD_ATOMIC_WEIGHT|||4|5|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d3 4s2|EVALUATED",
        "24|Cr|Chromium||51.996|STANDARD_ATOMIC_WEIGHT|||4|6|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d5 4s1|EVALUATED",
        "25|Mn|Manganese||54.938|STANDARD_ATOMIC_WEIGHT|||4|7|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d5 4s2|EVALUATED",
        "26|Fe|Iron|Ferrum|55.845|STANDARD_ATOMIC_WEIGHT|||4|8|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d6 4s2|EVALUATED",
        "27|Co|Cobalt||58.933|STANDARD_ATOMIC_WEIGHT|||4|9|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d7 4s2|EVALUATED",
        "28|Ni|Nickel||58.693|STANDARD_ATOMIC_WEIGHT|||4|10|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d8 4s2|EVALUATED",
        "29|Cu|Copper|Cuprum|63.546|STANDARD_ATOMIC_WEIGHT|||4|11|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s1|EVALUATED",
        "30|Zn|Zinc||65.38|STANDARD_ATOMIC_WEIGHT|||4|12|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s2|EVALUATED",
        "31|Ga|Gallium||69.723|STANDARD_ATOMIC_WEIGHT|||4|13|P|POST_TRANSITION_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s2 4p1|EVALUATED",
        "32|Ge|Germanium||72.630|STANDARD_ATOMIC_WEIGHT|||4|14|P|METALLOID|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s2 4p2|EVALUATED",
        "33|As|Arsenic||74.922|STANDARD_ATOMIC_WEIGHT|||4|15|P|METALLOID|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s2 4p3|EVALUATED",
        "34|Se|Selenium||78.971|STANDARD_ATOMIC_WEIGHT|||4|16|P|REACTIVE_NONMETAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s2 4p4|EVALUATED",
        "35|Br|Bromine||79.904|INTERVAL_STANDARD_ATOMIC_WEIGHT|79.901|79.907|4|17|P|REACTIVE_NONMETAL|MAIN_GROUP|LIQUID|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s2 4p5|EVALUATED",
        "36|Kr|Krypton||83.798|STANDARD_ATOMIC_WEIGHT|||4|18|P|NOBLE_GAS|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[Ar] 3d10 4s2 4p6|EVALUATED",
        "37|Rb|Rubidium||85.468|STANDARD_ATOMIC_WEIGHT|||5|1|S|ALKALI_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Kr] 5s1|EVALUATED",
        "38|Sr|Strontium||87.62|STANDARD_ATOMIC_WEIGHT|||5|2|S|ALKALINE_EARTH_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Kr] 5s2|EVALUATED",
        "39|Y|Yttrium||88.906|STANDARD_ATOMIC_WEIGHT|||5|3|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d1 5s2|EVALUATED",
        "40|Zr|Zirconium||91.224|STANDARD_ATOMIC_WEIGHT|||5|4|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d2 5s2|EVALUATED",
        "41|Nb|Niobium||92.906|STANDARD_ATOMIC_WEIGHT|||5|5|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d4 5s1|EVALUATED",
        "42|Mo|Molybdenum||95.95|STANDARD_ATOMIC_WEIGHT|||5|6|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d5 5s1|EVALUATED",
        "43|Tc|Technetium||98|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||5|7|D|TRANSITION_METAL|TRANSITION|SOLID|SYNTHETIC_RADIOACTIVE|[Kr] 4d5 5s2|EVALUATED",
        "44|Ru|Ruthenium||101.07|STANDARD_ATOMIC_WEIGHT|||5|8|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d7 5s1|EVALUATED",
        "45|Rh|Rhodium||102.91|STANDARD_ATOMIC_WEIGHT|||5|9|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d8 5s1|EVALUATED",
        "46|Pd|Palladium||106.42|STANDARD_ATOMIC_WEIGHT|||5|10|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10|EVALUATED",
        "47|Ag|Silver|Argentum|107.87|STANDARD_ATOMIC_WEIGHT|||5|11|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s1|EVALUATED",
        "48|Cd|Cadmium||112.41|STANDARD_ATOMIC_WEIGHT|||5|12|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s2|EVALUATED",
        "49|In|Indium||114.82|STANDARD_ATOMIC_WEIGHT|||5|13|P|POST_TRANSITION_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s2 5p1|EVALUATED",
        "50|Sn|Tin|Stannum|118.71|STANDARD_ATOMIC_WEIGHT|||5|14|P|POST_TRANSITION_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s2 5p2|EVALUATED",
        "51|Sb|Antimony|Stibium|121.76|STANDARD_ATOMIC_WEIGHT|||5|15|P|METALLOID|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s2 5p3|EVALUATED",
        "52|Te|Tellurium||127.60|STANDARD_ATOMIC_WEIGHT|||5|16|P|METALLOID|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s2 5p4|EVALUATED",
        "53|I|Iodine||126.90|STANDARD_ATOMIC_WEIGHT|||5|17|P|REACTIVE_NONMETAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s2 5p5|EVALUATED",
        "54|Xe|Xenon||131.29|STANDARD_ATOMIC_WEIGHT|||5|18|P|NOBLE_GAS|MAIN_GROUP|GAS|HAS_STABLE_ISOTOPES|[Kr] 4d10 5s2 5p6|EVALUATED",
        "55|Cs|Caesium||132.91|STANDARD_ATOMIC_WEIGHT|||6|1|S|ALKALI_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Xe] 6s1|EVALUATED",
        "56|Ba|Barium||137.33|STANDARD_ATOMIC_WEIGHT|||6|2|S|ALKALINE_EARTH_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Xe] 6s2|EVALUATED",
        "57|La|Lanthanum||138.91|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 5d1 6s2|EVALUATED",
        "58|Ce|Cerium||140.12|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f1 5d1 6s2|EVALUATED",
        "59|Pr|Praseodymium||140.91|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f3 6s2|EVALUATED",
        "60|Nd|Neodymium||144.24|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f4 6s2|EVALUATED",
        "61|Pm|Promethium||145|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||6||F|LANTHANIDE|LANTHANIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Xe] 4f5 6s2|EVALUATED",
        "62|Sm|Samarium||150.36|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f6 6s2|EVALUATED",
        "63|Eu|Europium||151.96|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f7 6s2|EVALUATED",
        "64|Gd|Gadolinium||157.25|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f7 5d1 6s2|EVALUATED",
        "65|Tb|Terbium||158.93|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f9 6s2|EVALUATED",
        "66|Dy|Dysprosium||162.50|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f10 6s2|EVALUATED",
        "67|Ho|Holmium||164.93|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f11 6s2|EVALUATED",
        "68|Er|Erbium||167.26|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f12 6s2|EVALUATED",
        "69|Tm|Thulium||168.93|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f13 6s2|EVALUATED",
        "70|Yb|Ytterbium||173.05|STANDARD_ATOMIC_WEIGHT|||6||F|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 6s2|EVALUATED",
        "71|Lu|Lutetium||174.97|STANDARD_ATOMIC_WEIGHT|||6|3|D|LANTHANIDE|LANTHANIDE|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d1 6s2|EVALUATED",
        "72|Hf|Hafnium||178.49|STANDARD_ATOMIC_WEIGHT|||6|4|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d2 6s2|EVALUATED",
        "73|Ta|Tantalum||180.95|STANDARD_ATOMIC_WEIGHT|||6|5|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d3 6s2|EVALUATED",
        "74|W|Tungsten|Wolfram|183.84|STANDARD_ATOMIC_WEIGHT|||6|6|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d4 6s2|EVALUATED",
        "75|Re|Rhenium||186.21|STANDARD_ATOMIC_WEIGHT|||6|7|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d5 6s2|EVALUATED",
        "76|Os|Osmium||190.23|STANDARD_ATOMIC_WEIGHT|||6|8|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d6 6s2|EVALUATED",
        "77|Ir|Iridium||192.22|STANDARD_ATOMIC_WEIGHT|||6|9|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d7 6s2|EVALUATED",
        "78|Pt|Platinum||195.08|STANDARD_ATOMIC_WEIGHT|||6|10|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d9 6s1|EVALUATED",
        "79|Au|Gold|Aurum|196.97|STANDARD_ATOMIC_WEIGHT|||6|11|D|TRANSITION_METAL|TRANSITION|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d10 6s1|EVALUATED",
        "80|Hg|Mercury|Hydrargyrum|200.59|STANDARD_ATOMIC_WEIGHT|||6|12|D|TRANSITION_METAL|TRANSITION|LIQUID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d10 6s2|EVALUATED",
        "81|Tl|Thallium||204.38|INTERVAL_STANDARD_ATOMIC_WEIGHT|204.382|204.385|6|13|P|POST_TRANSITION_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d10 6s2 6p1|EVALUATED",
        "82|Pb|Lead|Plumbum|207.2|INTERVAL_STANDARD_ATOMIC_WEIGHT|206.14|207.94|6|14|P|POST_TRANSITION_METAL|MAIN_GROUP|SOLID|HAS_STABLE_ISOTOPES|[Xe] 4f14 5d10 6s2 6p2|EVALUATED",
        "83|Bi|Bismuth||208.98|STANDARD_ATOMIC_WEIGHT|||6|15|P|POST_TRANSITION_METAL|MAIN_GROUP|SOLID|PRIMORDIAL_RADIOACTIVE|[Xe] 4f14 5d10 6s2 6p3|EVALUATED",
        "84|Po|Polonium||209|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||6|16|P|METALLOID|MAIN_GROUP|SOLID|PRIMORDIAL_RADIOACTIVE|[Xe] 4f14 5d10 6s2 6p4|EVALUATED",
        "85|At|Astatine||210|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||6|17|P|METALLOID|MAIN_GROUP|SOLID|PRIMORDIAL_RADIOACTIVE|[Xe] 4f14 5d10 6s2 6p5|EVALUATED",
        "86|Rn|Radon||222|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||6|18|P|NOBLE_GAS|MAIN_GROUP|GAS|PRIMORDIAL_RADIOACTIVE|[Xe] 4f14 5d10 6s2 6p6|EVALUATED",
        "87|Fr|Francium||223|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7|1|S|ALKALI_METAL|MAIN_GROUP|SOLID|PRIMORDIAL_RADIOACTIVE|[Rn] 7s1|EVALUATED",
        "88|Ra|Radium||226|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7|2|S|ALKALINE_EARTH_METAL|MAIN_GROUP|SOLID|PRIMORDIAL_RADIOACTIVE|[Rn] 7s2|EVALUATED",
        "89|Ac|Actinium||227|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|PRIMORDIAL_RADIOACTIVE|[Rn] 6d1 7s2|EVALUATED",
        "90|Th|Thorium||232.04|STANDARD_ATOMIC_WEIGHT|||7||F|ACTINIDE|ACTINIDE|SOLID|PRIMORDIAL_RADIOACTIVE|[Rn] 6d2 7s2|EVALUATED",
        "91|Pa|Protactinium||231.04|STANDARD_ATOMIC_WEIGHT|||7||F|ACTINIDE|ACTINIDE|SOLID|PRIMORDIAL_RADIOACTIVE|[Rn] 5f2 6d1 7s2|EVALUATED",
        "92|U|Uranium||238.03|STANDARD_ATOMIC_WEIGHT|||7||F|ACTINIDE|ACTINIDE|SOLID|PRIMORDIAL_RADIOACTIVE|[Rn] 5f3 6d1 7s2|EVALUATED",
        "93|Np|Neptunium||237|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f4 6d1 7s2|PREDICTED",
        "94|Pu|Plutonium||244|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f6 7s2|PREDICTED",
        "95|Am|Americium||243|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f7 7s2|PREDICTED",
        "96|Cm|Curium||247|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f7 6d1 7s2|PREDICTED",
        "97|Bk|Berkelium||247|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f9 7s2|PREDICTED",
        "98|Cf|Californium||251|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f10 7s2|PREDICTED",
        "99|Es|Einsteinium||252|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f11 7s2|PREDICTED",
        "100|Fm|Fermium||257|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f12 7s2|PREDICTED",
        "101|Md|Mendelevium||258|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f13 7s2|PREDICTED",
        "102|No|Nobelium||259|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7||F|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 7s2|PREDICTED",
        "103|Lr|Lawrencium||262|RADIOACTIVE_ISOTOPE_MASS_NUMBER|||7|3|D|ACTINIDE|ACTINIDE|SOLID|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 7s2 7p1|PREDICTED",
        "104|Rf|Rutherfordium||267|PREDICTED_OR_PROVISIONAL|||7|4|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d2 7s2|PROVISIONAL",
        "105|Db|Dubnium||268|PREDICTED_OR_PROVISIONAL|||7|5|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d3 7s2|PROVISIONAL",
        "106|Sg|Seaborgium||271|PREDICTED_OR_PROVISIONAL|||7|6|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d4 7s2|PROVISIONAL",
        "107|Bh|Bohrium||270|PREDICTED_OR_PROVISIONAL|||7|7|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d5 7s2|PROVISIONAL",
        "108|Hs|Hassium||277|PREDICTED_OR_PROVISIONAL|||7|8|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d6 7s2|PROVISIONAL",
        "109|Mt|Meitnerium||278|PREDICTED_OR_PROVISIONAL|||7|9|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d7 7s2|PROVISIONAL",
        "110|Ds|Darmstadtium||281|PREDICTED_OR_PROVISIONAL|||7|10|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d9 7s1|PROVISIONAL",
        "111|Rg|Roentgenium||282|PREDICTED_OR_PROVISIONAL|||7|11|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s1|PROVISIONAL",
        "112|Cn|Copernicium||285|PREDICTED_OR_PROVISIONAL|||7|12|D|TRANSITION_METAL|TRANSITION|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s2|PROVISIONAL",
        "113|Nh|Nihonium||286|PREDICTED_OR_PROVISIONAL|||7|13|P|POST_TRANSITION_METAL|MAIN_GROUP|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s2 7p1|PROVISIONAL",
        "114|Fl|Flerovium||289|PREDICTED_OR_PROVISIONAL|||7|14|P|POST_TRANSITION_METAL|MAIN_GROUP|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s2 7p2|PROVISIONAL",
        "115|Mc|Moscovium||290|PREDICTED_OR_PROVISIONAL|||7|15|P|POST_TRANSITION_METAL|MAIN_GROUP|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s2 7p3|PROVISIONAL",
        "116|Lv|Livermorium||293|PREDICTED_OR_PROVISIONAL|||7|16|P|POST_TRANSITION_METAL|MAIN_GROUP|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s2 7p4|PROVISIONAL",
        "117|Ts|Tennessine||294|PREDICTED_OR_PROVISIONAL|||7|17|P|METALLOID|MAIN_GROUP|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s2 7p5|PROVISIONAL",
        "118|Og|Oganesson||294|PREDICTED_OR_PROVISIONAL|||7|18|P|NOBLE_GAS|MAIN_GROUP|UNKNOWN|SYNTHETIC_RADIOACTIVE|[Rn] 5f14 6d10 7s2 7p6|PROVISIONAL"
    };

    @Test
    void generateDataFilesToTargetDirectory() throws Exception {
        // Output ONLY to target directory — never to tracked source files
        File targetDir = new File("target/generated-periodic-table");
        targetDir.mkdirs();
        File jsonDir = new File(targetDir, "chemistry-data");
        jsonDir.mkdirs();
        File sqlDir = new File(targetDir, "db/migration/chemistry");
        sqlDir.mkdirs();

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"datasetVersion\": \"v1.1.0\",\n");
        json.append("  \"generatedAt\": \"2026-08-04T00:00:00Z\",\n");
        json.append("  \"sources\": [\n");
        json.append("    {\"id\": \"CIAAW_STANDARD_ATOMIC_WEIGHTS_2021\", \"fields\": [\"atomicMass\", \"atomicMassKind\", \"atomicMassLowerBound\", \"atomicMassUpperBound\"]},\n");
        json.append("    {\"id\": \"NIST_GROUND_CONFIGURATIONS\", \"coverage\": \"Z=1..92\", \"fields\": [\"electronConfiguration\"]},\n");
        json.append("    {\"id\": \"IUPAC_PERIODIC_TABLE_2024\", \"fields\": [\"symbol\", \"name\", \"period\", \"group\", \"block\", \"category\", \"series\", \"standardState\"]},\n");
        json.append("    {\"id\": \"NUBASE2020\", \"fields\": [\"radioactivityStatus\"]}\n");
        json.append("  ],\n");
        json.append("  \"referenceConditions\": \"Standard Temperature and Pressure (STP): 273.15 K, 100 kPa\",\n");
        json.append("  \"elementRecords\": [\n");

        StringBuilder sql = new StringBuilder();
        sql.append("-- Seeding Periodic Table Reference Data v1.1.0\n");
        sql.append("INSERT INTO chemistry.periodic_table_catalog_versions (id, version, data_sources, reference_conditions) VALUES (\n");
        sql.append("  'v1.1.0', '1.1.0',\n");
        sql.append("  'CIAAW Standard Atomic Weights 2021; NIST Atomic Weights; IUPAC Periodic Table 2024; NUBASE2020',\n");
        sql.append("  'Standard Temperature and Pressure (STP): 273.15 K, 100 kPa'\n");
        sql.append(") ON CONFLICT (id) DO NOTHING;\n\n");

        assertThat(ELEMENT_DATA).hasSize(118);

        for (int i = 0; i < ELEMENT_DATA.length; i++) {
            String[] parts = ELEMENT_DATA[i].split("\\|", -1);
            int atomicNumber = Integer.parseInt(parts[0]);
            String symbol = parts[1];
            String name = parts[2];
            String latinName = parts[3].isEmpty() ? null : parts[3];
            BigDecimal massVal = new BigDecimal(parts[4]);
            String kind = parts[5];
            String lowerBound = parts[6].isEmpty() ? null : parts[6];
            String upperBound = parts[7].isEmpty() ? null : parts[7];
            int period = Integer.parseInt(parts[8]);
            Integer group = parts[9].isEmpty() ? null : Integer.parseInt(parts[9]);
            String block = parts[10];
            String category = parts[11];
            String series = parts[12];
            String state = parts[13];
            String radStatus = parts[14];
            String config = parts[15];
            String configStatus = parts[16].trim();

            // Verify registry alignment
            KnownElementRecord regRec = KnownElementRegistry.getByAtomicNumber(atomicNumber);
            assertThat(regRec).withFailMessage("Registry missing Z=%d", atomicNumber).isNotNull();
            assertThat(regRec.symbol()).withFailMessage("Z=%d symbol mismatch", atomicNumber).isEqualTo(symbol);

            // Verify Bismuth is PRIMORDIAL_RADIOACTIVE
            if (atomicNumber == 83) {
                assertThat(radStatus).isEqualTo("PRIMORDIAL_RADIOACTIVE");
            }

            // Verify interval elements have bounds
            if ("INTERVAL_STANDARD_ATOMIC_WEIGHT".equals(kind)) {
                assertThat(lowerBound).withFailMessage("Z=%d must have lower bound", atomicNumber).isNotNull();
                assertThat(upperBound).withFailMessage("Z=%d must have upper bound", atomicNumber).isNotNull();
            }

            // Verify no zero masses
            assertThat(massVal.compareTo(BigDecimal.ZERO)).withFailMessage("Z=%d mass must be > 0", atomicNumber).isGreaterThan(0);

            UUID elementId = UUID.nameUUIDFromBytes((symbol + "_" + atomicNumber).getBytes());

            json.append("    {\n");
            json.append("      \"atomicNumber\": ").append(atomicNumber).append(",\n");
            json.append("      \"symbol\": \"").append(symbol).append("\",\n");
            json.append("      \"name\": \"").append(name).append("\",\n");
            json.append("      \"latinName\": ").append(latinName == null ? "null" : "\"" + latinName + "\"").append(",\n");
            json.append("      \"atomicMass\": {\n");
            json.append("        \"representativeValue\": \"").append(massVal.toPlainString()).append("\",\n");
            json.append("        \"kind\": \"").append(kind).append("\",\n");
            json.append("        \"lowerBound\": ").append(lowerBound == null ? "null" : "\"" + lowerBound + "\"").append(",\n");
            json.append("        \"upperBound\": ").append(upperBound == null ? "null" : "\"" + upperBound + "\"").append("\n");
            json.append("      },\n");
            json.append("      \"period\": ").append(period).append(",\n");
            json.append("      \"group\": ").append(group == null ? "null" : group).append(",\n");
            json.append("      \"block\": \"").append(block).append("\",\n");
            json.append("      \"electronConfiguration\": \"").append(config).append("\",\n");
            json.append("      \"electronConfigurationStatus\": \"").append(configStatus).append("\",\n");
            json.append("      \"standardState\": \"").append(state).append("\",\n");
            json.append("      \"radioactivityStatus\": \"").append(radStatus).append("\",\n");
            json.append("      \"category\": \"").append(category).append("\",\n");
            json.append("      \"series\": \"").append(series).append("\"\n");
            json.append("    }").append(i < ELEMENT_DATA.length - 1 ? "," : "").append("\n");

            String escapedConfig = config.replace("'", "''");
            sql.append("INSERT INTO chemistry.elements (\n");
            sql.append("  id, atomic_number, symbol, name, latin_name,\n");
            sql.append("  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound,\n");
            sql.append("  period_number, group_number, block, electron_configuration, electron_configuration_status,\n");
            sql.append("  standard_state, radioactivity_status, category, series,\n");
            sql.append("  catalog_version_id, source_reference\n");
            sql.append(") VALUES (\n");
            sql.append("  '").append(elementId).append("', ")
               .append(atomicNumber).append(", '")
               .append(symbol).append("', '")
               .append(name).append("', ")
               .append(latinName == null ? "NULL" : "'" + latinName + "'").append(",\n  ")
               .append(massVal.toPlainString()).append(", '")
               .append(kind).append("', ")
               .append(lowerBound == null ? "NULL" : lowerBound).append(", ")
               .append(upperBound == null ? "NULL" : upperBound).append(",\n  ")
               .append(period).append(", ")
               .append(group == null ? "NULL" : group).append(", '")
               .append(block).append("', '")
               .append(escapedConfig).append("', '")
               .append(configStatus).append("',\n  '")
               .append(state).append("', '")
               .append(radStatus).append("', '")
               .append(category).append("', '")
               .append(series).append("',\n  ")
               .append("'v1.1.0', ")
               .append("'CIAAW/IUPAC/NIST/NUBASE2020'")
               .append("\n);\n");
        }

        json.append("  ]\n}\n");

        File jsonFile = new File(jsonDir, "periodic-table-core-v1.1.0.json");
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write(json.toString());
        }

        File sqlFile = new File(sqlDir, "V3__seed_periodic_table_core.sql");
        try (FileWriter fw = new FileWriter(sqlFile)) {
            fw.write(sql.toString());
        }

        // Verify output
        assertThat(jsonFile).exists();
        assertThat(sqlFile).exists();
        System.out.println("Generated JSON: " + jsonFile.getAbsolutePath());
        System.out.println("Generated SQL:  " + sqlFile.getAbsolutePath());
    }

    @Test
    void verifyBismuthIsNotClassifiedAsStable() {
        // Regression: Bismuth (Z=83) must be PRIMORDIAL_RADIOACTIVE
        String bismuthEntry = null;
        for (String entry : ELEMENT_DATA) {
            if (entry.startsWith("83|Bi|")) {
                bismuthEntry = entry;
                break;
            }
        }
        assertThat(bismuthEntry).isNotNull();
        assertThat(bismuthEntry).contains("PRIMORDIAL_RADIOACTIVE");
        assertThat(bismuthEntry).doesNotContain("HAS_STABLE_ISOTOPES");
        assertThat(bismuthEntry).doesNotContain("STABLE_OR_HAS_STABLE_ISOTOPES");
    }

    @Test
    void verifyIntervalElementsHaveBounds() {
        // H, Li, B, C, N, O, Mg, Si, S, Cl, Ar, Br, Tl, Pb must have interval entries
        String[] intervalSymbols = {"H", "Li", "B", "C", "N", "O", "Mg", "Si", "S", "Cl", "Ar", "Br", "Tl", "Pb"};
        for (String sym : intervalSymbols) {
            boolean found = false;
            for (String entry : ELEMENT_DATA) {
                String[] parts = entry.split("\\|", -1);
                if (parts[1].equals(sym)) {
                    assertThat(parts[5]).withFailMessage("%s should be INTERVAL_STANDARD_ATOMIC_WEIGHT", sym)
                            .isEqualTo("INTERVAL_STANDARD_ATOMIC_WEIGHT");
                    assertThat(parts[6]).withFailMessage("%s missing lower bound", sym).isNotEmpty();
                    assertThat(parts[7]).withFailMessage("%s missing upper bound", sym).isNotEmpty();
                    found = true;
                    break;
                }
            }
            assertThat(found).withFailMessage("Element %s not found in dataset", sym).isTrue();
        }
    }

    @Test
    void verifyNoZeroMassValues() {
        for (String entry : ELEMENT_DATA) {
            String[] parts = entry.split("\\|", -1);
            BigDecimal mass = new BigDecimal(parts[4]);
            assertThat(mass.compareTo(BigDecimal.ZERO)).withFailMessage("Zero mass found for %s", parts[1]).isGreaterThan(0);
        }
    }

    @Test
    void verifyDatasetVersionIs11() {
        // Dataset version should be v1.1.0 per this phase
        assertThat(ELEMENT_DATA).hasSize(118);
        // All entries should reference v1.1.0 configuration statuses
        for (String entry : ELEMENT_DATA) {
            String[] parts = entry.split("\\|", -1);
            String configStatus = parts.length > 16 ? parts[16].trim() : "";
            assertThat(configStatus).withFailMessage("Z=%s missing config status", parts[0])
                    .isIn("EVALUATED", "PREDICTED", "PROVISIONAL");
        }
    }
}

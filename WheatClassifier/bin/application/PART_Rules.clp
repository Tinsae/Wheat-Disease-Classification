(defrule prule1 
	(LesionShape RoundNarrowStripe)
	(FavorableConditions LowWet)
	(GrowthStage HardDough)
=>
	(assert (DiseaseType YellowRust)))

(defrule prule2 
	(LesionShape Elongated-Blister)
	(FavorableConditions ModerateWet)
=>
	(assert (DiseaseType LeafRust)))

(defrule prule3
	(DegreeofDamage SeedShrivelling)
	(LesionColor Tan)
=>
	(assert (DiseaseType Septoria)))

(defrule prule4
	(LesionShape OvalElongated)
	(LesionColor Red-Brown)
	(PartsAffected StemLsheathBlader) 
=>
	(assert (DiseaseType StemRust)))

(defrule prule5
	(LesionShape RoundNarrowStripe)
=>
	(assert (DiseaseType YellowRust)))

(defrule prule6
	(LesionColor Tan)
=>
	(assert (DiseaseType Septoria)))
(defrule prule7
	(PartsAffected LbladeSheath)
=>
	(assert (DiseaseType LeafRust)))

(defrule prule8
	(DegreeofDamage TeringOuterParts)
=>
	(assert (DiseaseType StemRust)))

(defrule prule9
=>
	(assert (DiseaseType StemRust)))



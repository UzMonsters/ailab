INSERT INTO chemistry.laboratory_safety_rules (
    rule_id, rule_version, rule_type, severity, evaluation_stage,
    operation_types, required_input_fields,
    condition_field, condition_operator, condition_target_value, condition_parameters,
    source_identifier, source_citation, source_version_date, evidence_status, effective_version, active
) VALUES
(
    'SAFE-FUME-HOOD-REQ', 1, 'FUME_HOOD_REQUIREMENT', 'CRITICAL', 'PRE_EXECUTION',
    '["STOICHIOMETRIC_REACTION","EQUILIBRIUM_REACTION","KINETIC_PROGRESS"]'::jsonb,
    '["fumeHoodOperating"]'::jsonb,
    'fumeHoodOperating', 'EQUALS', 'false', '{}'::jsonb,
    'OSHA-1910-1450', 'OSHA Laboratory Safety Standard (29 CFR 1910.1450)', '2026-01-01', 'VERIFIED', 1, true
),
(
    'SAFE-TEMP-LIMIT-GLASS', 1, 'CONTAINER_TEMPERATURE_LIMIT', 'CRITICAL', 'POST_CALCULATION',
    '["THERMAL_OPERATION","STOICHIOMETRIC_REACTION","EQUILIBRIUM_REACTION"]'::jsonb,
    '["temperatureK"]'::jsonb,
    'temperatureK', 'GREATER_THAN', '773.15', '{}'::jsonb,
    'ASTM-E438-92', 'ASTM E438-92 Standard Specification for Glasses in Laboratory Apparatus', '2026-01-01', 'VERIFIED', 1, true
),
(
    'SAFE-PRESSURE-LIMIT-CONTAINER', 1, 'CONTAINER_PRESSURE_LIMIT', 'CRITICAL', 'POST_CALCULATION',
    '["GAS_STATE_CHANGE","THERMAL_OPERATION"]'::jsonb,
    '["pressureKPa"]'::jsonb,
    'pressureKPa', 'GREATER_THAN', '200.0', '{}'::jsonb,
    'ISO-11114-1', 'ISO Gas Cylinders and Vessels Compatibility and Limits', '2026-01-01', 'VERIFIED', 1, true
)
ON CONFLICT (rule_id, rule_version) DO NOTHING;

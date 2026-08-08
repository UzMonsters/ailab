ALTER TABLE chemistry.laboratory_process_requirements
    DROP CONSTRAINT IF EXISTS laboratory_process_requirements_requirement_type_check;

ALTER TABLE chemistry.laboratory_process_requirements
    ADD CONSTRAINT laboratory_process_requirements_requirement_type_check
    CHECK (requirement_type IN ('MATERIAL','EQUIPMENT','CONTAINER','ENVIRONMENT','SCIENTIFIC_OPERATION'));

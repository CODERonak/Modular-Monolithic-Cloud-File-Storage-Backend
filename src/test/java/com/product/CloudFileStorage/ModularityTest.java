package com.product.CloudFileStorage;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTest {

    ApplicationModules modules = ApplicationModules.of(CloudFileStorageApplication.class);
    
    @Test
    void verifyModularStructure() {
        modules.verify();
    }

    @Test
    void documentModules() {
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}

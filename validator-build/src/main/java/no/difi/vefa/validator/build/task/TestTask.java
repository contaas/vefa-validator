package no.difi.vefa.validator.build.task;

import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.DirectorySource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class TestTask {
    private static Logger logger = LoggerFactory.getLogger(TestTask.class);

    public void test(Build build) throws Exception {
        Validator validator = null;

        try {
            validator = ValidatorBuilder
                    .newValidator()
                    .setProperties(new SimpleProperties()
                                    .set("feature.expectation", true)
                                    .set("feature.suppress_notloaded", true)
                    )
                    .setSource(new DirectorySource(build.getTargetFolder()))
                    .build();

            int tests = 0;
            int failed = 0;

            for (Path testFolder : build.getTestFolders()) {
                for (File file : FileUtils.listFiles(testFolder.toFile(), new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
                    if (!file.getName().equals("buildconfig.xml")) {
                        try {
                            Validation validation = validator.validate(file);
                            validation.getReport().setFilename(file.toString());

                            build.addTestValidation(validation);
                            tests++;

                            if (validation.getReport().getFlag().compareTo(FlagType.EXPECTED) > 0) {
                                logger.warn("Test '{}' ({})", file, validation.getReport().getFlag());
                                failed++;

                                for (SectionType sectionType : validation.getReport().getSection())
                                    for (AssertionType assertionType : sectionType.getAssertion())
                                        if (assertionType.getFlag().compareTo(FlagType.EXPECTED) > 0)
                                            logger.debug("  * {} {} ({})", assertionType.getIdentifier(), assertionType.getText(), assertionType.getFlag());
                            } else
                                logger.info("Test '{}'", file);
                        } catch (Exception e) {
                            logger.warn("Test '{}' ({})", file, e.getMessage());
                        }
                    }
                }
            }

            logger.info("{} tests performed, {} tests failed", tests, failed);
        } finally {
            if (validator != null)
                validator.close();
        }
    }
}

package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.util.DeclarationDetector;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UnCefactDeclarationTest {

    @Inject
    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector().injectMembers(this);
    }

    @Test
    public void simpleTC434() throws Exception {
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/uncefact-tc434.xml"));

        Assert.assertEquals(declarationDetector.detect(bytes).getIdentifier(), "CrossIndustryInvoice::urn:cen.eu:en16931:2017");
    }

    @Test
    public void simpleSimple() throws Exception {
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/uncefact-simple.xml"));

        Assert.assertEquals(declarationDetector.detect(bytes).getIdentifier(), "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice");
    }
}

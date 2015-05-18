package pl.pragmatists.concordion.rest.bootstrap;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class BootstrapExtension implements ConcordionExtension{

    @Override
    public void addTo(ConcordionExtender concordionExtender) {

        concordionExtender.withLinkedCSS("/bootstrap/bootstrap.css", new Resource("/bootstrap/bootstrap.css"));
        concordionExtender.withLinkedCSS("/bootstrap/enable-bootstrap.css", new Resource("/bootstrap/enable-bootstrap.css"));

    }

}

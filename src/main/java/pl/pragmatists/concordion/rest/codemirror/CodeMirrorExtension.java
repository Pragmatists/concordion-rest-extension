package pl.pragmatists.concordion.rest.codemirror;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class CodeMirrorExtension implements ConcordionExtension {

    @Override
    public void addTo(ConcordionExtender concordionExtender) {

        concordionExtender.withLinkedCSS("/codemirror/codemirror.css", new Resource("/codemirror/codemirror.css"));
        concordionExtender.withLinkedCSS("/codemirror/merge.css", new Resource("/codemirror/merge.css"));
        concordionExtender.withLinkedCSS("/codemirror/enable-codemirror.css", new Resource("/codemirror/enable-codemirror.css"));
        
        concordionExtender.withLinkedJavaScript("/codemirror/codemirror.js", new Resource("/codemirror/codemirror.js"));
        concordionExtender.withLinkedJavaScript("/codemirror/javascript.js", new Resource("/codemirror/javascript.js"));
        concordionExtender.withLinkedJavaScript("/codemirror/xml.js", new Resource("/codemirror/xml.js"));
        concordionExtender.withLinkedJavaScript("/codemirror/diff_match_patch.js", new Resource("/codemirror/diff_match_patch.js"));
        concordionExtender.withLinkedJavaScript("/codemirror/merge.js", new Resource("/codemirror/merge.js"));
        concordionExtender.withLinkedJavaScript("/codemirror/enable-codemirror.js", new Resource("/codemirror/enable-codemirror.js"));
        
    }
    
}

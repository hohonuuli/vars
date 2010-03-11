package vars.shared.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import vars.ILink;
import vars.annotation.AnnotationPersistenceService;
import vars.jpa.VarsJpaModule;
import vars.knowledgebase.LinkTemplate;
import vars.shared.ui.dialogs.StandardDialog;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class LinkSelectionPanelDemo {
	
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new VarsJpaModule("vars-jpa-annotation", "vars-jpa-knowledgebase", "vars-jpa-misc"));
		AnnotationPersistenceService annotationPersistenceService = injector.getInstance(AnnotationPersistenceService.class);
		Collection<LinkTemplate> linkTemplates = annotationPersistenceService.findLinkTemplatesFor(annotationPersistenceService.findRootConcept());
		Collection<ILink> links = Collections2.transform(linkTemplates, new Function<LinkTemplate, ILink>() {
			public ILink apply(LinkTemplate from) {
				return (ILink) from;
			}
		});
		LinkSelectionPanel panel = new LinkSelectionPanel(annotationPersistenceService);
		panel.setLinks(links);
		StandardDialog dialog = new StandardDialog();
		dialog.add(panel, BorderLayout.CENTER);
		ActionListener actionListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		dialog.getOkayButton().addActionListener(actionListener);
		dialog.getCancelButton().addActionListener(actionListener);
		dialog.pack();
		dialog.setVisible(true);
	}

}

package test.beast.app.beauti;

import static org.fest.swing.finder.JFileChooserFinder.findFileChooser;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.junit.Test;

import beast.app.beauti.BeautiConfig;
import beast.app.beauti.BeautiDoc;
import beast.app.beauti.BeautiMorphModelAlignmentProvider;
import beast.app.beauti.BeautiSubTemplate;
import beast.core.BEASTInterface;
import beast.evolution.alignment.Alignment;
import beast.util.NexusParser;
import beast.util.XMLParser;

public class BeautiMorphModelAlignmentProividerTest extends BeautiBase {
	
	JOptionPaneFixture dialog;

	// test whether an alignment with datatype=standard is loaded 
	// when charsets and ambiguities are specified
	@Test
	public void simpleTest2() throws Exception {
		JTabbedPaneFixture f = beautiFrame.tabbedPane();
		f.requireVisible();
		f.selectTab("Partitions");
		beautiFrame.button("+").click();

		dialog = new JOptionPaneFixture(robot());
		dialog.component().setLocation(0, 0);
		dialog.comboBox("OptionPane.comboBox").selectItem("Add MK Morphological Data");
		dialog.okButton().click();
		
		JFileChooserFixture fileChooser = findFileChooser().using(robot());
		fileChooser.setCurrentDirectory(new File("examples/nexus"));
		fileChooser.selectFiles(new File("M3982.nex")).approve();
		// close down any popup message
		robot().pressKey(KeyEvent.VK_ESCAPE);
	}

//	// test whether an alignment with datatype=standard is loaded
//	@Test
//	public void simpleTest1() throws Exception {
//		JTabbedPaneFixture f = beautiFrame.tabbedPane();
//		f.requireVisible();
//		f.selectTab("Partitions");
//		beautiFrame.button("+").click();
//
//		dialog = new JOptionPaneFixture(robot());
//		dialog.component().setLocation(0, 0);
//		dialog.comboBox("OptionPane.comboBox").selectItem("Add MK Morphological Data");
//		dialog.okButton().click();
//		
//		JFileChooserFixture fileChooser = findFileChooser().using(robot());
//		fileChooser.setCurrentDirectory(new File("examples/nexus"));
//		fileChooser.selectFiles(new File("M3982asInt.nex")).approve();
//		// close down any popup message
//		robot().pressKey(KeyEvent.VK_ESCAPE);
//	}
	
	
//	@Test
//	public void testMorphModel() throws Exception {
//		NexusParser parser = new NexusParser();
//		parser.parseFile(new File("examples/nexus/M3982.nex"));
//		Alignment alignment = parser.m_alignment;
//		
//		// create BeautiDoc and beauti configuration
//		BeautiDoc doc = new BeautiDoc();
//		doc.beautiConfig = new BeautiConfig();
//		doc.beautiConfig.initAndValidate();
//		
//		List<BEASTInterface> filteredAlignments = new ArrayList<BEASTInterface>();
//
//		BeautiMorphModelAlignmentProvider provider = new BeautiMorphModelAlignmentProvider();
//		XMLParser xmlparser = new XMLParser();
//		Object o = xmlparser.parseFile(new File("templates/morph-models.xml"));
//		BeautiSubTemplate subtemplate = (BeautiSubTemplate) o;
//		provider.template.setValue(subtemplate, provider);
//		
//		provider.processAlignment(alignment, filteredAlignments, doc);
//	}
}

package test.beast.app.beauti;

import static org.fest.swing.finder.JFileChooserFinder.findFileChooser;

import java.awt.event.KeyEvent;
import java.io.File;

import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.junit.Test;

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
	
}

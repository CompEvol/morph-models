package beast.app.beauti;

import beast.core.Description;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import beast.app.beauti.BeautiAlignmentProvider;
import beast.app.beauti.BeautiDoc;
import beast.app.draw.ExtensionFileFilter;
import beast.core.BEASTInterface;
import beast.evolution.alignment.Alignment;
import beast.evolution.datatype.StandardData;


@Description("Class for creating new partitions for morphological data to be edited by AlignmentListInputEditor")
public class MorphModelAlignmentProvider extends BeautiAlignmentProvider {
	

	@Override
	List<BEASTInterface> getAlignments(BeautiDoc doc) {
		JFileChooser fileChooser = new JFileChooser(Beauti.g_sDir);
		String[] exts = { ".nex", ".nxs", ".nexus" };
		fileChooser.addChoosableFileFilter(new ExtensionFileFilter(exts, "Nexus file (*.nex)"));

		fileChooser.setDialogTitle("Load Sequence");
		fileChooser.setMultiSelectionEnabled(true);
		int rval = fileChooser.showOpenDialog(null);

		if (rval == JFileChooser.APPROVE_OPTION) {

			File[] files = fileChooser.getSelectedFiles();
			
			// TODO insert code to split up alignment into FilteredAlignemnts here

            return getAlignments(doc, files);
		}
		return null;
	}
	
	@Override
	int matches(Alignment alignment) {
		if (alignment.userDataTypeInput.get() != null && 
				alignment.userDataTypeInput.get() instanceof StandardData ) {
				return 10;
		}
		return 0;
	}
	
	
//	@Override
//	void editAlignment(Alignment alignment, BeautiDoc doc) {
//	}

}

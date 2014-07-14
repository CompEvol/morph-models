package beast.app.beauti;

import beast.core.Description;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import beast.app.beauti.BeautiAlignmentProvider;
import beast.app.beauti.BeautiDoc;
import beast.app.draw.ExtensionFileFilter;
import beast.core.BEASTInterface;
import beast.evolution.alignment.Alignment;
import beast.evolution.datatype.DataType;
import beast.evolution.datatype.StandardData;
import beast.evolution.datatype.UserDataType;


@Description("Class for creating new partitions for morphological data to be edited by AlignmentListInputEditor")
public class BeautiMorphModelAlignmentProvider extends BeautiAlignmentProvider {
	

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
			
			// split alignments into filtered alignments -- one for each state space size
            List<BEASTInterface> alignments = getAlignments(doc, files);
    		List<BEASTInterface> filteredAlignments = new ArrayList<BEASTInterface>();
    		try {
            for (BEASTInterface o : alignments) {
            	if (o instanceof Alignment) {
					processAlignment((Alignment) o, filteredAlignments, doc);
            	}
            }
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Something went wrong converting the alignment: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
            
            return filteredAlignments;
		}
		return null;
	}
	
	// split an alignment into filtered alignments -- one for each state space size
	// add them to filteredAlignments
	private void processAlignment(Alignment alignment, List<BEASTInterface> filteredAlignments, BeautiDoc doc) throws Exception {
		Map<Integer, List<Integer>> stateSpaceMap = new HashMap<Integer, List<Integer>>();
		
		// distinguish between StandardData and others
		if (alignment.getDataType() instanceof StandardData) {
			// determine state space size by interogating StandardData data-type 
			StandardData dataType = (StandardData) alignment.getDataType();
			for (int i = 0; i < alignment.getSiteCount(); i++) {
				// TODO: this assumes there is a charStateLabel for this site -- deal with the case there is not such charStateLabel 
				int nrOfStates = 0;
				if (dataType.charStateLabelsInput.get().size() > i) {
					nrOfStates = dataType.charStateLabelsInput.get().get(i).getNrOfStates();
				} else {
					nrOfStates = calcNumberOfStates(alignment, i);
				}
				if (!stateSpaceMap.containsKey(nrOfStates)) {
					stateSpaceMap.put(nrOfStates, new ArrayList<Integer>());
				}
				stateSpaceMap.get(nrOfStates).add(i);
			}
		} else {
			// determine state space size by counting different nr of states for a 
			for (int i = 0; i < alignment.getSiteCount(); i++) {
				int nrOfStates = calcNumberOfStates(alignment, i);
				if (!stateSpaceMap.containsKey(nrOfStates)) {
					stateSpaceMap.put(nrOfStates, new ArrayList<Integer>());
				}
				stateSpaceMap.get(nrOfStates).add(i);
			}
		}
		
		String tree = alignment.getID();
		String clock = alignment.getID();
		
		// create filtered alignments
		for (Integer nrOfStates : stateSpaceMap.keySet()) {
			String ID = alignment.getID() + nrOfStates;
			
			// create fileter range
			// currently, just creates a singleton for each entry.
			// The entries are sorted (by construction of the list)
			// TODO: beautify range when there are consecutive sites involved 
			StringBuilder range = new StringBuilder();
			for (Integer site : stateSpaceMap.get(nrOfStates)) {
				range.append(site + ",");
			}
			range.deleteCharAt(range.length() - 1);
			
			// create data type
			DataType dataType;
			if (alignment.getDataType() instanceof StandardData) {
				// determine state space size by interogating StandardData data-type 
				StandardData base = (StandardData) alignment.getDataType();
				dataType = new StandardData();
				((StandardData) dataType).initByName("statecount", nrOfStates,
						"base", base); // TODO inmlement base input in StandardData
			} else {
				// TODO deal with ambiguous codes
				StringBuilder codeMap = new StringBuilder();
				for (int i = 0; i < nrOfStates; i++) {
					codeMap.append(i +" = " + i + ", ");
				} 
				codeMap.append("? =");
				for (int i = 0; i < nrOfStates; i++) {
					codeMap.append(" " + i);
				}				
				UserDataType userDataType = new UserDataType();
				userDataType.initByName("states", nrOfStates,
						"codelength", 1,
						"codeMap", codeMap);
				dataType = userDataType;
			}
			
            // link trees and clock models
			String name = alignment.getID() + nrOfStates;
			PartitionContext context = new PartitionContext(name, name, clock, tree);

            // create treelikelihood for each state space
			Alignment fAlignment = (Alignment) doc.addAlignmentWithSubnet(context, template.get());
            fAlignment.setID(ID);
            fAlignment.initByName("alignment", alignment,
            		"range", range.toString(),
            		"userDataTYpe", dataType
            		);
            filteredAlignments.add(fAlignment);
		}
	
	}

	/** calculate number of states for a site by determining the set of unique
	 * characters at that site. 
	 */
	private int calcNumberOfStates(Alignment alignment, int site) {
		int [] pattern = alignment.getPattern(alignment.getPatternIndex(site));
		Set<Integer> states = new HashSet<Integer>();
		for (int k : pattern) {
			if (!alignment.getDataType().isAmbiguousState(k)) {
				states.add(k);
			}
		}
		int nrOfStates = states.size();
		return nrOfStates;
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

package beast.app.beauti;

import beast.core.Description;

import java.io.File;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import beast.app.draw.ExtensionFileFilter;
import beast.core.BEASTInterface;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AscertainedForParsimonyUninformativeFilteredAlignment;
import beast.evolution.alignment.FilteredAlignment;
import beast.evolution.alignment.Sequence;
import beast.evolution.datatype.DataType;
import beast.evolution.datatype.StandardData;
import beast.evolution.datatype.UserDataType;
import beast.util.NexusParser;

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

			// split alignments into filtered alignments -- one for each state
			// space size
			List<BEASTInterface> alignments = getAlignments(doc, files);
            int partitions = 0; //JOptionPane.showConfirmDialog(null, "Would you like to partition the data matrix with respect to the number of character states \n " +
                    //"to apply different substitution models for each partition?", "Data partition with respect to the number of states", 0);
			List<BEASTInterface> filteredAlignments = new ArrayList<BEASTInterface>();
            int condition = 1; //JOptionPane.showConfirmDialog(null, "Would you like to condition on recording variable characters only (Mkv)?", "Conditioning on constant characters", 0);
            if (partitions == 0) {
                try {
                    for (BEASTInterface o : alignments) {
                        if (o instanceof Alignment) {
                            if (condition == 0) {
                                processAlignment((Alignment) o, filteredAlignments, true, doc);
                            }  else {
                                processAlignment((Alignment) o, filteredAlignments, false, doc);
                            }
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Something went wrong converting the alignment: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }

                return filteredAlignments;
            }  else {
                //TODO figure out what to return  implement not partitioning alignmnet 1) ascertainment
            }

		}
		return null;
	}

	@Override
	public List<BEASTInterface> getAlignments(BeautiDoc doc, File[] files) {
		List<BEASTInterface> selectedPlugins = new ArrayList<BEASTInterface>();
		for (File file : files) {
			String fileName = file.getName();
			// if (sFileName.lastIndexOf('/') > 0) {
			// Beauti.g_sDir = sFileName.substring(0,
			// sFileName.lastIndexOf('/'));
			// }
			if (fileName.toLowerCase().endsWith(".nex") || fileName.toLowerCase().endsWith(".nxs") || fileName.toLowerCase().endsWith(".nexus")) {
				NexusParser parser = new NexusParser();
				try {
					parser.parseFile(file);
					if (parser.filteredAlignments.size() > 0) {
						/**
						 * sanity check: make sure the filters do not overlap
						 **/
						int[] used = new int[parser.m_alignment.getSiteCount()];
						Set<Integer> overlap = new HashSet<Integer>();
						int partitionNr = 1;
						for (Alignment data : parser.filteredAlignments) {
							int[] indices = ((FilteredAlignment) data).indices();
							for (int i : indices) {
								if (used[i] > 0) {
									overlap.add(used[i] * 10000 + partitionNr);
								} else {
									used[i] = partitionNr;
								}
							}
							partitionNr++;
						}
						if (overlap.size() > 0) {
							String overlaps = "<html>Warning: The following partitions overlap:<br/>";
							for (int i : overlap) {
								overlaps += parser.filteredAlignments.get(i / 10000 - 1).getID() + " overlaps with "
										+ parser.filteredAlignments.get(i % 10000 - 1).getID() + "<br/>";
							}
							overlaps += "The first thing you might want to do is delete some of these partitions.</html>";
							JOptionPane.showMessageDialog(null, overlaps);
						}
						/** add alignments **/
						for (Alignment data : parser.filteredAlignments) {
							selectedPlugins.add(data);
						}
					} else {
						selectedPlugins.add(parser.m_alignment);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Loading of " + fileName + " failed: " + ex.getMessage());
					return null;
				}
			}
		}
		return selectedPlugins;
	}

	// split an alignment into filtered alignments -- one for each state space
	// size
	// add them to filteredAlignments
	public void processAlignment(Alignment alignment, List<BEASTInterface> filteredAlignments, boolean ascertained, BeautiDoc doc) throws Exception {
		Map<Integer, List<Integer>> stateSpaceMap = new HashMap<Integer, List<Integer>>();

        int initialSiteCount = alignment.getSiteCount();
        int maxNrOfStates = 0; // for each site the number of states is counted and the maximum of this number is MaxNrOfStates
		int seqCount = alignment.sequenceInput.get().size();

		// distinguish between StandardData and others
		if (alignment.getDataType() instanceof StandardData) {
			// determine state space size by interogating StandardData data-type
			StandardData dataType = (StandardData) alignment.getDataType();
			for (int i = 0; i < alignment.getSiteCount(); i++) {
				int nrOfStates = 0;
				int nrOfStatesPresented = calcNumberOfStates(alignment, i);
				if (dataType.charStateLabelsInput.get().size() > i && dataType.charStateLabelsInput.get().get(i).getStateCount() > 0) {
					// this assumes there is a charStateLabel with the state description for this site
					nrOfStates = dataType.charStateLabelsInput.get().get(i).getStateCount();
					if (nrOfStatesPresented > nrOfStates) {
						throw new Exception("The number of states in character " + (i+1) + " is larger than in " +
								"the description. It should be less or equal.");
					}
				} else {
					// deal with the case where there is no charStateLabel or there is no state description
					nrOfStates = nrOfStatesPresented;
				}
				if (!stateSpaceMap.containsKey(nrOfStates)) {
					stateSpaceMap.put(nrOfStates, new ArrayList<Integer>());
                    maxNrOfStates = Math.max(maxNrOfStates, nrOfStates);
				}
				stateSpaceMap.get(nrOfStates).add(i);
			}
		} else {
			// determine state space size by counting states in each site
			for (int i = 0; i < alignment.getSiteCount(); i++) {
				int nrOfStates = calcNumberOfStates(alignment, i);
				if (!stateSpaceMap.containsKey(nrOfStates)) {
					stateSpaceMap.put(nrOfStates, new ArrayList<Integer>());
                    maxNrOfStates = Math.max(maxNrOfStates, nrOfStates);
				}
				stateSpaceMap.get(nrOfStates).add(i);
			}
		}

        if (ascertained) {
            StringBuilder seqToAccountForAscertainment = new StringBuilder();
            for (int i=0; i<maxNrOfStates; i++) {
                seqToAccountForAscertainment.append(i);
            }
			int seqIndex=0;
            for (Sequence seq: alignment.sequenceInput.get()) {
				StringBuilder seqNonConstant = new StringBuilder();
				for (int i=0; i<seqCount; i++) {
					if (i == seqIndex) {
						seqNonConstant.append("1");
					} else {
						seqNonConstant.append("0");
					}

				}
				seqIndex++;
                String newSequenceValue = seq.dataInput.get() + seqToAccountForAscertainment + seqNonConstant;
                seq.dataInput.setValue(newSequenceValue, seq);
            }
            alignment.initAndValidate(); //TODO look if we need to initAndValidate or just update some members of alignment
        }


		String tree = alignment.getID();
		String clock = alignment.getID();

		// create filtered alignments
		for (Integer nrOfStates : stateSpaceMap.keySet()) {
			String ID = alignment.getID() + nrOfStates;

			// create fileter range
			// currently, just creates a singleton for each entry.
			// The entries are sorted (by construction of the list)
            // AG: added ranges for consequent site numbers
			StringBuilder range = new StringBuilder();
            List<Integer> sites = stateSpaceMap.get(nrOfStates);
            for (int i=0; i< sites.size(); i++) {
                int site = sites.get(i);
                if (i == sites.size()-1 || sites.get(i+1) != site+1) {
                    range.append((site + 1)+ ",");
                } else {
                    if (range.length() == 0 || range.charAt(range.length()-1) != '-') {
                        range.append((site + 1)+ "-");
                    }
                }

            }
//			for (Integer site : sites) {
//				range.append((site + 1)+ ",");
//			}

            if (ascertained) {
                range.append((initialSiteCount+1)+"-"+(initialSiteCount+nrOfStates)+",");
				range.append((initialSiteCount+maxNrOfStates+1)+"-"+(initialSiteCount+maxNrOfStates+seqCount)+",");
            }
			range.deleteCharAt(range.length() - 1);

			// create data type
			DataType.Base dataType;
			if (alignment.getDataType() instanceof StandardData) {
				// determine state space size by interogating StandardData
				// data-type
				StandardData base = (StandardData) alignment.getDataType();
				dataType = new StandardData();
				((StandardData) dataType).initByName("nrOfStates", nrOfStates, 
						"ambiguities", base.listOfAmbiguitiesInput.get());
				// "base", base); // TODO inmlement base input in StandardData
			} else {
				// TODO deal with ambiguous codes
				StringBuilder codeMap = new StringBuilder();
				for (int i = 0; i < nrOfStates; i++) {
					codeMap.append(i + " = " + i + ", ");
				}
				codeMap.append("? =");
				for (int i = 0; i < nrOfStates; i++) {
					codeMap.append(" " + i);
				}
				UserDataType userDataType = new UserDataType();
				userDataType.initByName("states", nrOfStates, "codelength", 1, "codeMap", codeMap);
				dataType = userDataType;
			}

			String name = alignment.getID() + nrOfStates;
			dataType.setID("morphDataType." + name);
			doc.addPlugin(dataType);
            FilteredAlignment data = ascertained?new AscertainedForParsimonyUninformativeFilteredAlignment():new FilteredAlignment();

            if (ascertained) {
				data.isAscertainedInput.setValue(true, data);
				data.excludefromInput.setValue(stateSpaceMap.get(nrOfStates).size(), data);
				data.excludetoInput.setValue(stateSpaceMap.get(nrOfStates).size()+nrOfStates, data);
				((AscertainedForParsimonyUninformativeFilteredAlignment)data).excludefromNonConstantInput.setValue(stateSpaceMap.get(nrOfStates).size()+nrOfStates, data);
				((AscertainedForParsimonyUninformativeFilteredAlignment)data).excludetoNonConstantInput.setValue(stateSpaceMap.get(nrOfStates).size()+nrOfStates+seqCount, data);

            }
			data.initByName("data", alignment, "filter", range.toString(), "userDataType", dataType);
			data.setID(ID);
			doc.addPlugin(data);
			
			
			// link trees and clock models
			PartitionContext context = new PartitionContext(name, name, clock, tree);

			// create treelikelihood for each state space
			try {
				doc.addAlignmentWithSubnet(context, template.get());
//				GeneralSubstitutionModel smodel = (GeneralSubstitutionModel) doc.pluginmap.get("morphSubstModel.s:" + name);
//				((RealParameter) smodel.ratesInput.get()).setDimension(nrOfStates * (nrOfStates - 1)/2);
//				smodel.frequenciesInput.get().frequenciesInput.get().setDimension(nrOfStates);
//				SiteModelInterface.Base sitemodel = (SiteModelInterface.Base) doc.pluginmap.get("morphSiteModel.s:" + name);
//				sitemodel.substModelInput.setValue(smodel, sitemodel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			filteredAlignments.add(data);
		}

	}

	/**
	 * calculate number of states for a site by determining the set of unique
	 * characters at that site.
	 */
	private int calcNumberOfStates(Alignment alignment, int site) throws Exception{
		int[] pattern = alignment.getPattern(alignment.getPatternIndex(site));
		Set<Integer> states = new HashSet<Integer>();
		DataType dataType = alignment.getDataType();
		for (int k : pattern) {
			if (k >= 0 && !dataType.getCode(k).equals("?") && !dataType.getCode(k).equals("-")) {
				for (int m:dataType.getStatesForCode(k)) {
					states.add(m);
				}
			}
		}
		int nrOfStates = states.size();
		return nrOfStates;
	}

	@Override
	int matches(Alignment alignment) {
		if (alignment.userDataTypeInput.get() != null && alignment.userDataTypeInput.get() instanceof StandardData) {
			return 20;
		}
		return 0;
	}

	// @Override
	// void editAlignment(Alignment alignment, BeautiDoc doc) {
	// }

}

/*
 * NexusBlocksUtils.java Copyright (C) 2026 Daniel H. Huson
 *
 *  (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package phylofusion.utils;

import jloda.phylo.PhyloTree;
import jloda.util.BitSetUtils;
import jloda.util.IteratorUtils;
import splitstree6.data.TaxaBlock;
import splitstree6.data.TreesBlock;

import java.util.Collection;
import java.util.TreeMap;

public class NexusBlocksUtils {

	public static Result setupBlocks(Collection<PhyloTree> trees) {
		return setupBlocks(createTaxaBlock(trees), trees);
	}

	public static Result setupBlocks(TaxaBlock taxaBlock, Collection<PhyloTree> phyloTrees) {
		var treesBlock = new TreesBlock();

		var partial = phyloTrees.stream().mapToInt(t -> IteratorUtils.count(t.getTaxa())).allMatch(c -> c < taxaBlock.getNtax());

		treesBlock.setRooted(true);
		treesBlock.setPartial(partial);
		treesBlock.setReticulated(false);
		treesBlock.getTrees().addAll(phyloTrees);
		return new Result(taxaBlock, treesBlock);
	}

	public static TaxaBlock createTaxaBlock(Collection<PhyloTree> trees) {
		var taxaBlock = new TaxaBlock();

		var idLabelMap = new TreeMap<Integer, String>();
		for (var tree : trees) {
			for (var v : tree.nodes()) {
				if (v.isLeaf()) {
					var id = tree.getTaxon(v);
					var label = tree.getLabel(v);
					if (label == null || label.isBlank())
						throw new RuntimeException("Unlabeled leaf encountered");
					idLabelMap.put(id, label);
				}
			}
		}
		var set = BitSetUtils.asBitSet(idLabelMap.keySet());
		if (set.cardinality() != BitSetUtils.max(set) - BitSetUtils.min(set) + 1)
			System.err.println("Wrong set");
		for (var t = 1; t <= idLabelMap.size(); t++) {
			taxaBlock.addTaxonByName(idLabelMap.get(t));
		}
		return taxaBlock;
	}

	public record Result(TaxaBlock taxaBlock, TreesBlock treesBlock) {
	}

}

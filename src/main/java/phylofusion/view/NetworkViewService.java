/*
 * NetworkView.java Copyright (C) 2026 Daniel H. Huson
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

package phylofusion.window;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;
import jloda.graph.Edge;
import jloda.graph.Node;
import jloda.phylo.PhyloTree;
import jloda.phylogeny.layout.Averaging;
import splitstree6.data.TaxaBlock;
import splitstree6.layout.tree.ComputeTreeLayout;
import splitstree6.layout.tree.LabeledEdgeShape;
import splitstree6.layout.tree.LabeledNodeShape;
import splitstree6.layout.tree.TreeDiagramType;

import java.util.HashMap;
import java.util.Map;

public class NetworkView {
	private final Pane pane;
	private final Map<Node, LabeledNodeShape> nodeLabeledNodeShapeMap=new HashMap<>();
	private final Map<Edge, LabeledEdgeShape> edgeLabeledEdgeShapeHashMap=new HashMap<>();

	public NetworkView(Pane pane) {
		this.pane=pane;
	}

	public void update(TaxaBlock taxaBlock, PhyloTree network, TreeDiagramType diagram,
					   Averaging averaging) {
		var taxonLabelMap=new HashMap<Integer, StringProperty>();
		for(var t=1;t<=taxaBlock.getNtax();t++) {
			taxonLabelMap.put(t,new SimpleStringProperty(taxaBlock.getLabel(t)));
		}
		var width=Math.max(400,pane.getWidth()-50);
		var height= Math.max(400,pane.getHeight()-50);
		var alignLabels=true;
		ComputeTreeLayout.apply(network, taxaBlock.getNtax(), taxonLabelMap::get,diagram,averaging,width,height,alignLabels,
				nodeLabeledNodeShapeMap,edgeLabeledEdgeShapeHashMap,true);
	}
}

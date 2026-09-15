# Data and scripts for the phylogenetic parallelograms paper

This directory contains the input trees, the SplitsTree and PhyloParallelograms
documents, and the scripts used to produce the figures of

> Daniel H. Huson, Banu Cetinkaya and Louxin Zhang.
> *Phylogenetic parallelograms: visual comparison of discordant phylogenetic trees.*
> Manuscript, 2026.

All parallelograms were computed with PhyloParallelograms
(https://github.com/husonlab/phyloparallelograms, version 1.1.2) and all
tanglegrams with the SplitsTree App (https://github.com/husonlab/splitstree6,
version 6.7 or later); the exact versions are given in the Methods of the paper.

## Layout

There is one subdirectory per figure of the paper. Fig. 7, which shows the
user interface, uses the data of `figure4`, and Fig. 8, the node labelling,
has no data.

| Directory | Figure | Data set | Source |
|-----------|--------|----------|--------|
| `figure1` | Fig. 1 | Species tree and whole-genome tree of six species of the *Anopheles gambiae* complex | Fontaine et al. 2015, Fig. 1B |
| `figure2` | Fig. 2 | Two rooted trees on five taxa used to illustrate the scaffold construction | this paper |
| `figure3` | Fig. 3 | Four pairs of synthetic trees on 20 taxa at Robinson–Foulds distances 2, 12, 26 and 32 (panels a–d), and tanglegram displacement and scaffold hybridization number for 1,700 synthetic tree pairs with the scripts to recompute and plot them (panel e) | de Vienne 2019 |
| `figure4` | Figs. 4 and 7 | Gene trees for 15 loci of the *Anopheles gambiae* complex | alignments of Fontaine et al. 2015 |
| `figure5` | Fig. 5 | Plastid and nuclear trees for 31 orchid genera; chloroplast and ITS trees for 69 Danthonioideae taxa | Pérez-Escobar et al. 2021; Pirie et al. 2009 |
| `figure6` | Fig. 6 | Plastid and nuclear trees for 129 Fagaceae taxa | Zhou et al. 2022 |

## File types

Each data set is provided in up to three forms, named `<dataset>.tre`,
`<dataset>-tanglegram.stree6` and `<dataset>-parallelogram.phypar`:

- `<dataset>.tre`: the rooted input trees in Newick format, one tree per
  line. Tree names are given as `[&&NHX:GN=<name>]` comments following each
  tree. These files are the primary data; the two other files are derived
  from them.
- `<dataset>-tanglegram.stree6`: a SplitsTree App document containing the
  trees and the displacement-optimized tanglegram shown in the figure. Open
  it with *File > Open* in the SplitsTree App.
- `<dataset>-parallelogram.phypar`: a PhyloParallelograms document containing
  the trees, the settings, the scaffold network with its tree-membership
  annotations, and the parallelogram shown in the figure. Open it with
  *File > Open* in PhyloParallelograms. To recompute the parallelogram from
  scratch, open the `.tre` file instead and choose *Layout > Run PhyloFusion
  Layout*.

## Figure by figure

### figure1: two trees for the *Anopheles gambiae* complex

`mosquitoes.tre` contains the species tree (`Species_topology`), which is
supported by the distal part of the X chromosome, and the whole-genome tree
(`Whole-genome`) for *An. coluzzii*, *An. gambiae*, *An. arabiensis*,
*An. quadriannulatus*, *An. melas* and *An. merus*, transcribed from Fig. 1B
of Fontaine et al. (2015). `mosquitoes-tanglegram.stree6` and
`mosquitoes-parallelogram.phypar` are the documents behind Fig. 1a and 1b.

### figure2: the two trees of the worked example

`two-trees-for-alts.tre` contains the trees T1 = ((a,(b,(e,d))),c) and
T2 = ((a,b),((e,d),c)) of Fig. 2. For this input PhyloParallelograms
chooses the taxon ordering a < b < c < d < e, under which the scaffold has a
single reticulation; opening the file in PhyloParallelograms reproduces the
parallelogram shown in the figure.

### figure3: synthetic tree pairs and the complexity benchmark

`Vienne-<RF>-1.tre` is the pair `1.tre` from directory `RF_<RF>` of the data
set `AllPairs` distributed as supplementary material with de Vienne (2019),
for RF = 2, 12, 26 and 32. Each file contains two rooted trees on the taxa
t1 to t20 whose Robinson–Foulds distance is RF. The `-tanglegram.stree6` and
`-parallelogram.phypar` documents are panels a to d of Fig. 3.

`rf_td_h.tsv` contains, for all 1,700 tree pairs analysed (100 pairs for each
Robinson–Foulds distance 2, 4, ..., 34), the minimum total displacement of
the displacement-optimized tanglegram and the hybridization number of the
scaffold. It is the Source Data of Fig. 3e. The scripts that compute the
table and draw the plot are described in `figure3/README.md`.

### figure4: gene trees for 15 loci of the *Anopheles gambiae* complex

`mosquitoes-loci.tre` contains maximum-likelihood trees for 15 loci of 3 to
7 kb, rooted on *An. christyi*, with bootstrap support values. The loci were
extracted from the whole-genome alignment of Fontaine et al. (2015), Dryad
doi:10.5061/dryad.f4114, and the trees were computed with IQ-TREE as
described step by step at https://github.com/husonlab/trees-to-networks-tutorial,
which also provides the alignments and the locus coordinates. The tree names
give the chromosomal region of the locus: `X_dist` (distal X chromosome),
`X_peri` (pericentromeric X chromosome), `auto_2R` and `auto_3R` (autosomal
arms 2R and 3R), and `inv_2La` and `inv_3La` (the 2La and 3La inversions).
`mosquitoes-loci-parallelogram.phypar` is the document behind Fig. 4. Fig. 7,
which shows the user interface, was made from the same document with only
the five X-distal trees and the pericentromeric tree selected and the
minimum branch support set to 50%. The same data set is shipped with
PhyloParallelograms as an example (`examples/mosquitos-loci.phypar`).

### figure5: cytonuclear discordance in orchids and in Danthonioideae

`Orchids-Genera.tre` contains the plastid and nuclear trees (`Plastid`,
`Nuclear`) for 31 orchid genera derived from the phylogenies of
Pérez-Escobar et al. (2021). `Danthonioideae.tre` contains the chloroplast
and ITS trees (`Chloroplast`, `ITS`) for 69 taxa of Danthonioideae from
Pirie et al. (2009), with branch lengths. The corresponding
`-tanglegram.stree6` and `-parallelogram.phypar` documents are the four
panels of Fig. 5. The Danthonioideae data set is also shipped with
PhyloParallelograms as an example (`examples/Danthonioideae.phypar`).

### figure6: cytonuclear discordance in Fagaceae

`Fagaceae.tre` contains the chloroplast and nuclear trees (`Chloroplast`,
`Nuclear`) for 129 Fagaceae taxa, with branch lengths and support values,
recomputed from data provided by the authors of Zhou et al. (2022). The
`-tanglegram.stree6` and `-parallelogram.phypar` documents are the two
panels of Fig. 6.

## How the orchid and Fagaceae trees were produced

A description of how the genus-level orchid trees (figure5) and the Fagaceae
trees (figure6) were derived from the published data, together with the
scripts used, will be added to the respective directories.

## References

- de Vienne, D. M. Tanglegrams are misleading for visual evaluation of tree
  congruence. *Mol. Biol. Evol.* 36, 174–176 (2019).
  https://doi.org/10.1093/molbev/msy196
- Fontaine, M. C. et al. Extensive introgression in a malaria vector species
  complex revealed by phylogenomics. *Science* 347, 1258524 (2015).
  https://doi.org/10.1126/science.1258524
- Huson, D. H. Displacement-optimized tanglegrams for trees and networks.
  *Mol. Biol. Evol.* 43, msag066 (2026). https://doi.org/10.1093/molbev/msag066
- Huson, D. H. & Bryant, D. The SplitsTree App: interactive analysis and
  visualization using phylogenetic trees and networks. *Nat. Methods* 21,
  1773–1774 (2024).
- Pérez-Escobar, O. A. et al. Hundreds of nuclear and plastid loci yield novel
  insights into orchid relationships. *Am. J. Bot.* 108, 1166–1180 (2021).
- Pirie, M. D., Humphreys, A. M., Barker, N. P. & Linder, H. P. Reticulation,
  data combination, and inferring evolutionary history: an example from
  Danthonioideae (Poaceae). *Syst. Biol.* 58, 612–628 (2009).
  https://doi.org/10.1093/sysbio/syp060
- Zhang, L., Cetinkaya, B. & Huson, D. H. PhyloFusion: fast and easy fusion of
  rooted phylogenetic trees into rooted phylogenetic networks. *Syst. Biol.*
  75, 88–102 (2026). https://doi.org/10.1093/sysbio/syaf049
- Zhou, B.-F. et al. Phylogenomic analyses highlight innovation and
  introgression in the continental radiations of Fagaceae across the Northern
  Hemisphere. *Nat. Commun.* 13, 1320 (2022).
  https://doi.org/10.1038/s41467-022-28917-1

## Reuse

The scripts are released under the GNU General Public License v3, like the
rest of this repository. The tree files derived from published studies are
provided so that the figures can be reproduced; please cite the original
publications listed above when reusing them.

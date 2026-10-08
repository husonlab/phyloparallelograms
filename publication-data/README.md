# Data and scripts for the phylogenetic parallelograms paper

This directory contains the input trees, the SplitsTree and PhyloParallelograms
documents, and the scripts used to produce the figures of

> Daniel H. Huson, Banu Cetinkaya and Louxin Zhang.
> *Phylogenetic parallelograms: visual comparison of discordant phylogenetic trees.*
> Manuscript submitted to Systematic Biology, 2026.

All parallelograms were computed with PhyloParallelograms
(https://github.com/husonlab/phyloparallelograms, version 1.1.2) and all
tanglegrams with the SplitsTree App (https://github.com/husonlab/splitstree6,
version 6.7 or later); the exact versions are given in the Methods of the paper.

## Layout

There is one subdirectory per figure of the main text (`figure1` to
`figure4`) and per figure of the Supplementary Material (`figureS1`,
`figureS2`). The file `Supplementary_Material.pdf` is the Supplementary
Material of the paper: it describes the node labeling, the handling of
multifurcations and missing taxa, branch lengths, the layout, and the
preparation of the data sets, and contains Figs. S1 and S2 and Tables S1
and S2. It is included in the Dryad deposit.

| Directory | Figure | Data set | Source |
|-----------|--------|----------|--------|
| `figure1` | Fig. 1 | Gene trees for 15 loci of the *Anopheles gambiae* complex (panel a) and the subset of six trees shown in the user interface (panel b) | alignments of Fontaine et al. 2015 |
| `figure2` | Fig. 2 | Species tree and whole-genome tree of six species of the *Anopheles gambiae* complex (panels a, b); two rooted trees on five taxa used to illustrate the scaffold construction (panel c) | Fontaine et al. 2015, Fig. 1B; this paper |
| `figure3` | Fig. 3 | Chloroplast and ITS trees for 69 Danthonioideae taxa (panels a, b); plastid and nuclear trees for 129 Fagaceae taxa (panels c, d) | Pirie et al. 2009; Zhou et al. 2022 |
| `figure4` | Fig. 4 | Four pairs of synthetic trees on 20 taxa at Robinson–Foulds distances 2, 12, 26 and 32 (panels a–d), and tanglegram displacement and scaffold hybridization number for 1,700 synthetic tree pairs with the scripts to recompute and plot them (panel e) | de Vienne 2019 |
| `figureS1` | Fig. S1 | The tree T1 of the worked example, used to illustrate the node labeling | this paper |
| `figureS2` | Fig. S2 | Nuclear and mitogenome trees of the living cats (43 taxa in total) | Li et al. 2016 |

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

### figure1: gene trees for 15 loci of the *Anopheles gambiae* complex

`mosquitoes-loci.tre` contains maximum-likelihood trees for 15 loci of 3 to
7 kb, rooted on *An. christyi*, with bootstrap support values. The loci were
extracted from the whole-genome alignment of Fontaine et al. (2015), Dryad
doi:10.5061/dryad.f4114, and the trees were computed with IQ-TREE as
described step by step at https://github.com/husonlab/trees-to-networks-tutorial,
which also provides the alignments and the locus coordinates. The tree names
give the chromosomal region of the locus: `X_dist` (distal X chromosome),
`X_peri` (pericentromeric X chromosome), `auto_2R` and `auto_3R` (autosomal
arms 2R and 3R), and `inv_2La` and `inv_3La` (the 2La and 3La inversions).
`mosquitoes-loci-parallelogram.phypar` is the document behind Fig. 1a.
`mosquitoes-loci-subset-parallelogram.phypar` is the document shown in the
user interface in Fig. 1b: the same data with only the five X-distal trees and
the pericentromeric tree selected and the minimum branch support set to 50%.
The data set is also shipped with PhyloParallelograms as an example
(`examples/mosquitos-loci.phypar`).

### figure2: two trees for the *Anopheles gambiae* complex and the worked example

`mosquitoes.tre` contains the species tree (`Species_topology`), which is
supported by the distal part of the X chromosome, and the whole-genome tree
(`Whole-genome`) for *An. coluzzii*, *An. gambiae*, *An. arabiensis*,
*An. quadriannulatus*, *An. melas* and *An. merus*, transcribed from Fig. 1B
of Fontaine et al. (2015). `mosquitoes-tanglegram.stree6` and
`mosquitoes-parallelogram.phypar` are the documents behind Fig. 2a and 2b.

`two-trees-for-alts.tre` contains the trees T1 = ((a,(b,(e,d))),c) and
T2 = ((a,b),((e,d),c)) of Fig. 2c. For this input PhyloParallelograms
chooses the taxon ordering a < b < c < d < e, under which the scaffold has a
single reticulation; opening the file in PhyloParallelograms reproduces the
parallelogram shown in the figure.

### figure3: organellar discordance in Danthonioideae and in Fagaceae

`Danthonioideae.tre` contains the chloroplast and ITS trees (`Chloroplast`,
`ITS`) for 69 taxa of Danthonioideae from Pirie et al. (2009), with branch
lengths. The corresponding `-tanglegram.stree6` and `-parallelogram.phypar`
documents are panels a and b of Fig. 3. The data set is also shipped with
PhyloParallelograms as an example (`examples/Danthonioideae.phypar`).

`Fagaceae.tre` contains the chloroplast and nuclear trees (`Chloroplast`,
`Nuclear`) for 129 Fagaceae taxa, with branch lengths and support values,
recomputed from the MrBayes-formatted data sets provided by Zhou et al. (2022)
on Dryad (https://doi.org/10.5061/dryad.vq83bk3tc). The `-tanglegram.stree6`
and `-parallelogram.phypar` documents are panels c and d of Fig. 3, which
omit the taxon labels; the labels are present in these files.

How the Fagaceae trees were produced (B. Cetinkaya): Bayesian analyses were
performed in MrBayes v3.2.6 (Ronquist et al. 2012) using the partitioning
schemes and substitution models specified in the supplied NEXUS files. MCMC
analyses were run for 10 million generations, sampling trees every 100
generations, with the first 25% of samples discarded as burn-in. *Betula
pendula* (`Betula_pendula_MG386401`) was used as the outgroup. Because the two
data sets use different taxon labels, the labels were matched by hand using
the sample information provided by Zhou et al., and labels that could not be
matched were left unchanged.

### figure4: synthetic tree pairs and the complexity benchmark

`Vienne-<RF>-1.tre` is the pair `1.tre` from directory `RF_<RF>` of the data
set `AllPairs` distributed as supplementary material with de Vienne (2019),
for RF = 2, 12, 26 and 32. Each file contains two rooted trees on the taxa
t1 to t20 whose Robinson–Foulds distance is RF. The `-tanglegram.stree6` and
`-parallelogram.phypar` documents are panels a to d of Fig. 4.

`rf_td_h.tsv` contains, for all 1,700 tree pairs analysed (100 pairs for each
Robinson–Foulds distance 2, 4, ..., 34), the minimum total displacement of
the displacement-optimized tanglegram and the hybridization number of the
scaffold. It is the source data of Fig. 4e. The scripts that compute the
table and draw the plot are described in `figure4/README.md`.

### figureS1: the tree of the node-labeling example

`one-tree.tre` contains the tree T1 = ((a,(b,(e,d))),c) of the worked
example, whose node labeling under the ordering a < b < c < d < e is built
up step by step in Fig. S1 of the Supplementary Material.

### figureS2: nuclear and mitogenome trees of cats

`cats.tre` contains the nuclear (`Nuclear`, 42 tips) and mitogenome
(`Mitogenome`, 39 tips) trees of the living cats, transcribed from Fig. 1A of
Li et al. (2016) with the image-capture function of PhyloSketch (Huson 2025)
after all elements other than tree edges and leaf labels had been removed from
the figure and the coloured and dashed edges converted to solid black lines.
The nuclear tree includes two Indochinese and one Sundaic sample of the Asian
leopard cat and the Sunda clouded leopard, which are absent from the
mitogenome tree; the mitogenome tree has a single Asian leopard cat. The five
taxa present in only one tree were retained, so the scaffold is computed with
the missing-taxa procedure of PhyloFusion. The cleaned images and the scripts
used to prepare them are provided as an example of image capture with
PhyloSketch at
https://github.com/husonlab/phylosketch2/tree/main/examples/examples-capture-cats/cats.
The `-tanglegram.stree6` and `-parallelogram.phypar` documents are the two
panels of Fig. S2.

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
- Pirie, M. D., Humphreys, A. M., Barker, N. P. & Linder, H. P. Reticulation,
  data combination, and inferring evolutionary history: an example from
  Danthonioideae (Poaceae). *Syst. Biol.* 58, 612–628 (2009).
  https://doi.org/10.1093/sysbio/syp068
- Huson, D. H. Sketch, capture and layout phylogenies. *PLOS Comput. Biol.*
  21, e1013805 (2025). https://doi.org/10.1371/journal.pcbi.1013805
- Li, G., Davis, B. W., Eizirik, E. & Murphy, W. J. Phylogenomic evidence for
  ancient hybridization in the genomes of living cats (Felidae). *Genome Res.*
  26, 1–11 (2016). https://doi.org/10.1101/gr.186668.114
- Ronquist, F. et al. MrBayes 3.2: efficient Bayesian phylogenetic inference
  and model choice across a large model space. *Syst. Biol.* 61, 539–542
  (2012). https://doi.org/10.1093/sysbio/sys029
- Zhang, L., Cetinkaya, B. & Huson, D. H. PhyloFusion: fast and easy fusion of
  rooted phylogenetic trees into rooted phylogenetic networks. *Syst. Biol.*
  75, 88–99 (2026). https://doi.org/10.1093/sysbio/syaf049
- Zhou, B.-F. et al. Phylogenomic analyses highlight innovation and
  introgression in the continental radiations of Fagaceae across the Northern
  Hemisphere. *Nat. Commun.* 13, 1320 (2022).
  https://doi.org/10.1038/s41467-022-28917-1

## Reuse

The scripts are released under the GNU General Public License v3, like the
rest of this repository. The tree files derived from published studies are
provided so that the figures can be reproduced; please cite the original
publications listed above when reusing them.

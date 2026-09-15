# Figure 3: synthetic tree pairs and the complexity benchmark

Panels a to d of Fig. 3 show four synthetic tree pairs as tanglegrams and
parallelograms (files `Vienne-<RF>-1.tre`, `Vienne-<RF>-1-tanglegram.stree6`
and `Vienne-<RF>-1-parallelogram.phypar` in this directory; see the README in
the parent directory). Panel e is the complexity benchmark described here.

## Panel e: complexity of tanglegrams and parallelograms versus RF distance

Panel e compares, for 1,700 pairs of synthetic trees on 20 taxa (100 pairs for
each Robinson–Foulds distance 2, 4, ..., 34), the minimum total displacement
(TD) of a displacement-optimized tanglegram with the hybridization number (H)
of the PhyloFusion scaffold underlying the phylogenetic parallelogram.

## Files

- `rf_td_h.tsv`: the results, one row per tree pair, with columns `RF`
  (Robinson–Foulds distance), `TD` (minimum total displacement of the
  tanglegram) and `H` (hybridization number of the scaffold). This table is
  the Source Data of Fig. 3e; the plot can be reproduced from it directly (step
  4 below) without rerunning the computation.
- `tanglegram-phylofusion.wflow6`: a SplitsTree workflow that reads one tree
  pair, computes the displacement-optimized tanglegram (which reports TD) and
  runs PhyloFusion on the two trees (which reports the hybridization number).
- `run-workflow.sh`: runs this workflow on one tree file with the
  `workflow-run` tool of SplitsTree.
- `collect-rf-td-h.sh`: runs `run-workflow.sh` on all tree pairs and
  collects RF, TD and H into `rf_td_h.tsv`.
- `plot-rf-td-h.py`: draws the plot from the table.

## Reproducing panel e

1. Download the data set `AllPairs` from the supplementary material of
   de Vienne (2019) and unpack it into this directory. It contains the
   directories `RF_2`, `RF_4`, ..., `RF_34`, each holding 1,000 files
   `1.tre` to `1000.tre` with two rooted trees on the taxa t1 to t20 at the
   given Robinson–Foulds distance. The paper uses the pairs `100.tre` to
   `199.tre` of every directory.
2. Install the SplitsTree command-line tools, version 6.9.5 or later, from
   https://github.com/husonlab/splitstree6/releases, and edit
   `run-workflow.sh` so that it calls the installed `workflow-run` script.
3. Run `./collect-rf-td-h.sh`. This writes `rf_td_h.tsv`. The script accepts
   the data directory, the workflow script and the output file as optional
   arguments, in this order (defaults: `AllPairs`, `./run-workflow.sh`,
   `rf_td_h.tsv`). Pairs for which TD or H could not be parsed are reported
   and skipped, and the tool output is saved as `<pair>.failed.log`.
4. Run `python3 plot-rf-td-h.py --boxplot rf_td_h.tsv -o rf_td_h.pdf`. This
   requires Python 3 with pandas and matplotlib. The `--boxplot` option
   produces the box plots shown in the paper; without it, violin plots are
   drawn.

The four tree pairs shown in panels a to d are the files `1.tre` of `RF_2`,
`RF_12`, `RF_26` and `RF_32`.

## Reference

de Vienne, D. M. Tanglegrams are misleading for visual evaluation of tree
congruence. *Mol. Biol. Evol.* 36, 174–176 (2019).
https://doi.org/10.1093/molbev/msy196

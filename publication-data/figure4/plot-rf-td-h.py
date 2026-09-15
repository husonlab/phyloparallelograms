#!/opt/homebrew/bin/python3.12

import argparse
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.patches import Patch


def color_violin(vp, color):
    for body in vp["bodies"]:
        body.set_facecolor(color)
        body.set_edgecolor("black")
        body.set_alpha(0.65)
    for key in ("cbars", "cmins", "cmaxes", "cmedians"):
        if key in vp:
            vp[key].set_color("black")
            vp[key].set_linewidth(0.8)


def draw_boxplots(ax, data, positions, color):
    common_props = dict(color="black", linewidth=0.9)
    return ax.boxplot(
        data,
        positions=positions,
        widths=0.30,
        patch_artist=True,
        showfliers=False,
        boxprops=dict(facecolor=color, edgecolor="black", linewidth=0.9, alpha=0.65),
        whiskerprops=common_props,
        capprops=common_props,
        medianprops=dict(color="black", linewidth=1.2)
    )


def main():
    parser = argparse.ArgumentParser(
        description="Plot tanglegram displacement and scaffold hybridization number versus RF distance."
    )
    parser.add_argument("input", help="TSV file with columns RF, TD, H")
    parser.add_argument("-o", "--output", default="rf_td_h.pdf")
    parser.add_argument(
        "--boxplot",
        action="store_true",
        help="Draw boxplots instead of violin plots."
    )
    args = parser.parse_args()

    df = pd.read_csv(args.input, sep="\t")

    required = {"RF", "TD", "H"}
    missing = required.difference(df.columns)
    if missing:
        raise SystemExit(f"Input file is missing required column(s): {', '.join(sorted(missing))}")

    df["RF"] = df["RF"].astype(int)

    rfs = sorted(df["RF"].unique())
    td_data = [df[df["RF"] == rf]["TD"].values for rf in rfs]
    h_data = [df[df["RF"] == rf]["H"].values for rf in rfs]

    positions = list(range(len(rfs)))
    positions_td = [p - 0.18 for p in positions]
    positions_h = [p + 0.18 for p in positions]

    fig, ax = plt.subplots(figsize=(11, 5))

    if args.boxplot:
        draw_boxplots(ax, td_data, positions_td, "C0")
        draw_boxplots(ax, h_data, positions_h, "C1")
    else:
        vp_td = ax.violinplot(
            td_data,
            positions=positions_td,
            widths=0.30,
            showmeans=False,
            showmedians=True,
            showextrema=True
        )
        vp_h = ax.violinplot(
            h_data,
            positions=positions_h,
            widths=0.30,
            showmeans=False,
            showmedians=True,
            showextrema=True
        )
        color_violin(vp_td, "C0")
        color_violin(vp_h, "C1")

    ax.set_xticks(positions)
    ax.set_xticklabels([str(rf) for rf in rfs])
    ax.set_xlabel("Robinson–Foulds distance")
    ax.set_ylabel("Count")
    ax.set_title("Complexity of displacement-optimized tanglegrams and phylogenetic parallelograms")

    handles = [
        Patch(facecolor="C0", edgecolor="black", alpha=0.65, label="Tanglegram total displacement"),
        Patch(facecolor="C1", edgecolor="black", alpha=0.65, label="Parallelogram reticulations"),
    ]
    ax.legend(handles=handles, frameon=False)

    ax.grid(axis="y", alpha=0.3)
    fig.tight_layout()
    fig.savefig(args.output, bbox_inches="tight")
    print(f"Wrote {args.output}")


if __name__ == "__main__":
    main()


#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="${1:-AllPairs}"
WORKFLOW="${2:-./run-workflow.sh}"
OUT="${3:-rf_td_h.tsv}"

echo -e "RF\tTD\tH" > "$OUT"

for dir in "$BASE_DIR"/RF_*; do
    [[ -d "$dir" ]] || continue
    rf="${dir##*/RF_}"

    for file in "$dir"/1??.tre; do
        [[ -f "$file" ]] || continue

        output="$("$WORKFLOW" "$file" 2>&1 || true)"

        h="$(printf '%s\n' "$output" \
            | sed -n 's/.*Hybridization number:[[:space:]]*\([0-9][0-9]*\).*/\1/p' \
            | tail -n 1)"

        td="$(printf '%s\n' "$output" \
            | sed -n 's/.*TD=\([0-9][0-9]*\).*/\1/p' \
            | tail -n 1)"

        echo "Processing RF=$rf file=$file: TD=$td H=$h" >&2

        if [[ -z "$h" || -z "$td" ]]; then
            echo "WARNING: could not parse H or TD from $file" >&2
            echo "$output" > "${file%.tre}.failed.log"
            continue
        fi

        echo -e "${rf}\t${td}\t${h}" >> "$OUT"
    done
done

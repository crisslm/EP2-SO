#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
classes_dir="$repo_root/target/benchmark-regression-classes"
mkdir -p "$classes_dir"

# Apenas o núcleo do benchmark: dispensa Swing/JFreeChart e dependências Maven.
mapfile -d '' sources < <(find "$repo_root/src/main/java/usp" \
    -type f -name '*.java' \
    ! -path '*/Graph/*' ! -path '*/Graph2/*' \
    ! -name 'Main.java' ! -name 'Exercicio.java' -print0)

javac --release 21 -d "$classes_dir" "${sources[@]}" \
    "$repo_root/src/test/java/usp/BenchmarkRegressionChecks.java"
java -cp "$classes_dir" usp.BenchmarkRegressionChecks

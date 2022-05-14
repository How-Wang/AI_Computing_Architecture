#!/bin/bash
# Helper script to elaborate a Chisel module that has no parameters.

set -e

package="$1"
module="$2"
parameters="$3"

[ ! -d "./top_entry" ] && mkdir "top_entry"

cat <<EOF > ./top_entry/top_from_script.scala
package top

import ${package}._

object Elaborate extends App {
  (
    new chisel3.stage.ChiselStage).emitVerilog(
      new ${module}($parameters),
      Array("-td","generated/${module}")
  )
}
EOF

echo "Elaborating module ${module}..."
sbt "runMain top.Elaborate -td ./generated/${module}"
echo "Elaborated Verilog available at generated/${module}"

exit 0

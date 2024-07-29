package info.mking.k2zpl.command

import info.mking.k2zpl.builder.ZplBuilder
import info.mking.k2zpl.builder.command

internal data object FieldSeparator : ZplCommand {
    override val command: CharSequence = "^FS"
}

/**
 * Adds a field separator.
 */
fun ZplBuilder.fieldSeparator() {
    command(FieldSeparator)
}
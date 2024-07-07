package info.mking.k2zpl.builder

import info.mking.k2zpl.command.ZplCommand
import info.mking.k2zpl.command.options.ZplDpiSetting
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.math.roundToInt

class ZplBuilderTest : DescribeSpec({

    isolationMode = IsolationMode.InstancePerTest

    val mockZplCommand: ZplCommand = mockk(relaxed = true) {
        every { command } returns "an-command"
    }

    val subject = ZplBuilder()

    describe("addCommand") {
        it("should use the passed command without parameters") {
            subject.addCommand(mockZplCommand)
            subject.build() shouldBe "an-command\n"
        }
        it("should use the passed command with parameters") {
            every { mockZplCommand.parameters } returns mapOf("an" to "param")
            subject.addCommand(mockZplCommand)
            subject.build() shouldBe "an-commandparam\n"
        }
        it("should used the pass string command") {
            subject.addCommand("another-command")
            subject.build() shouldBe "another-command\n"
        }
    }
    describe("dpiSetting") {
        it("throws an appropriate exception when Unset") {
            shouldThrow<IllegalStateException> { subject.dpiSetting }
        }
        it("accepts the value set") {
            subject.dpiSetting = ZplDpiSetting.DPI_203
            subject.dpiSetting shouldBe ZplDpiSetting.DPI_203
        }
    }
    describe("Int mm extension") {
        it("throws an appropriate exception when no ZplDpiSetting") {
            shouldThrow<IllegalStateException> {
                subject.apply { 1.mm }
            }
        }
        it("translates the correct amount to dots") {
            val table = table(
                headers("dpi", "num", "expected"),
                (ZplDpiSetting.entries - ZplDpiSetting.Unset).flatMap { dpi ->
                    (1..10).map { row(dpi, it, (it * dpi.dotsPerMm).roundToInt()) }
                }
            )
            subject.apply {
                table.forAll { zplDpiSetting, num, expected ->
                    subject.dpiSetting = zplDpiSetting
                    num.mm shouldBe expected
                }
            }
        }
    }
    describe("Int cm extension") {
        val table = table(
            headers("dpi", "num", "expected"),
            (ZplDpiSetting.entries - ZplDpiSetting.Unset).flatMap { dpi ->
                (1..10).map { row(dpi, it, (it * 10 * dpi.dotsPerMm).roundToInt()) }
            }
        )
        it("throws an appropriate exception when no ZplDpiSetting") {
            shouldThrow<IllegalStateException> {
                subject.apply { 1.cm }
            }
        }
        it("translates the correct amount to dots") {
            subject.apply {
                table.forAll { zplDpiSetting, num, expected ->
                    subject.dpiSetting = zplDpiSetting
                    num.cm shouldBe expected
                }
            }
        }
    }
    describe("Int inches extension") {
        it("throws an appropriate exception when no ZplDpiSetting") {
            shouldThrow<IllegalStateException> {
                subject.apply { 1.inches }
            }
        }
        it("translates the correct amount to dots") {
            val table = table(
                headers("dpi", "num", "expected"),
                (ZplDpiSetting.entries - ZplDpiSetting.Unset).flatMap { dpi ->
                    (1..10).map { row(dpi, it, it * dpi.dpi) }
                }
            )
            subject.apply {
                table.forAll { zplDpiSetting, num, expected ->
                    subject.dpiSetting = zplDpiSetting
                    num.inches shouldBe expected
                }
            }
        }
    }

})
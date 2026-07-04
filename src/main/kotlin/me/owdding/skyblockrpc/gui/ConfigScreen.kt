package me.owdding.skyblockrpc.gui

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import me.owdding.skyblockrpc.SkyBlockRPC
import me.owdding.skyblockrpc.config.Config
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractSliderButton
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth

class ConfigScreen(private val parent: Screen? = null) : Screen(Component.literal("SkyBlock RPC")) {

    private companion object {
        const val PW = 460
        const val PH = 322
        val PANEL   = 0xFF333436.toInt()
        val INNER   = 0xFF27282A.toInt()
        val MUTED   = 0xFF7A8090.toInt()
        val ACCENT  = 0xFF5865F2.toInt()
        val BORDER  = 0xFF444648.toInt()
        val WHITE   = 0xFFFFFFFF.toInt()
        val WARN    = 0xFFE8A838.toInt()
        val OVERLAY = 0xCC0D0E0F.toInt()
    }

    private var customTextBox: EditBox? = null
    private var clientIdBox: EditBox? = null
    private var px = 0
    private var py = 0

    override fun init() {
        px = (width - PW) / 2
        py = (height - PH) / 2
        val fx = px + 20
        val fw = PW - 40

        customTextBox = EditBox(font, fx + 1, py + 57, fw - 2, 16, Component.empty()).also {
            it.value = Config.customText
            it.setMaxLength(100)
            addRenderableWidget(it)
        }

        val initSlider = (Config.timeBetweenRotations - 5).toDouble() / 55.0
        addRenderableWidget(object : AbstractSliderButton(fx, py + 112, fw, 18, Component.empty(), initSlider) {
            init { updateMessage() }
            override fun updateMessage() {
                setMessage(Component.literal("${Mth.clamp((value * 55 + 5).toInt(), 5, 60)}s"))
            }
            override fun applyValue() {
                Config.timeBetweenRotations = Mth.clamp((value * 55 + 5).toInt(), 5, 60)
            }
        })

        addRenderableWidget(
            Button.builder(Component.literal(Config.logo.displayName)) { btn ->
                val logos = SkyBlockRPC.Logo.entries
                val next = logos[(logos.indexOf(Config.logo) + 1) % logos.size]
                Config.logo = next
                btn.message = Component.literal(next.displayName)
            }.bounds(fx, py + 162, fw, 18).build()
        )

        clientIdBox = EditBox(font, fx + 1, py + 215, fw - 2, 16, Component.empty()).also {
            it.value = Config.clientId
            it.setMaxLength(25)
            addRenderableWidget(it)
        }

        val halfW = (fw - 8) / 2
        addRenderableWidget(
            Button.builder(Component.literal("Edit Lines & Buttons")) {
                minecraft?.setScreen(ResourcefulConfigScreen.getFactory(SkyBlockRPC.MOD_ID).apply(this))
            }.bounds(fx, py + PH - 48, halfW, 20).build()
        )

        addRenderableWidget(
            Button.builder(Component.literal("Save & Close")) {
                Config.customText = customTextBox?.value ?: Config.customText
                Config.clientId   = clientIdBox?.value   ?: Config.clientId
                onClose()
            }.bounds(fx + halfW + 8, py + PH - 48, halfW, 20).build()
        )
    }

    override fun extractRenderState(g: GuiGraphicsExtractor, mx: Int, my: Int, pt: Float) {
        g.fill(0, 0, width, height, OVERLAY)
        fillRounded(g, px, py, px + PW, py + PH, 8, PANEL)
        fillRounded(g, px, py, px + PW, py + 30, 8, ACCENT)
        g.fill(px, py + 22, px + PW, py + 30, ACCENT)
        g.text(font, "SkyBlock RPC  ·  v${SkyBlockRPC.VERSION}", width / 2 - font.width("SkyBlock RPC  ·  v${SkyBlockRPC.VERSION}") / 2, py + 10, WHITE, true)

        val fx = px + 20
        val fw = PW - 40

        g.text(font, "Custom Text", fx, py + 42, MUTED, false)
        fillRounded(g, fx, py + 54, fx + fw, py + 76, 4, INNER)
        g.fill(fx, py + 74, fx + fw, py + 75, ACCENT)

        g.text(font, "Time Between Rotations", fx, py + 98, MUTED, false)
        g.text(font, "Logo", fx, py + 148, MUTED, false)

        g.text(font, "Client ID", fx, py + 200, MUTED, false)
        val warnText = "⚠ Restart to apply"
        g.text(font, warnText, fx + fw - font.width(warnText), py + 200, WARN, false)
        fillRounded(g, fx, py + 212, fx + fw, py + 234, 4, INNER)
        g.fill(fx, py + 232, fx + fw, py + 233, ACCENT)

        g.fill(px + 16, py + PH - 56, px + PW - 16, py + PH - 55, BORDER)
        super.extractRenderState(g, mx, my, pt)
    }

    private fun fillRounded(g: GuiGraphicsExtractor, x1: Int, y1: Int, x2: Int, y2: Int, r: Int, c: Int) {
        g.fill(x1 + r, y1, x2 - r, y2, c)
        g.fill(x1, y1 + r, x1 + r, y2 - r, c)
        g.fill(x2 - r, y1 + r, x2, y2 - r, c)
        val h = r / 2
        g.fill(x1 + h, y1 + 1, x1 + r, y1 + h, c)
        g.fill(x2 - r, y1 + 1, x2 - h, y1 + h, c)
        g.fill(x1 + h, y2 - h, x1 + r, y2 - 1, c)
        g.fill(x2 - r, y2 - h, x2 - h, y2 - 1, c)
    }

    override fun onClose() { minecraft?.setScreen(parent) }
    override fun isPauseScreen() = false
}

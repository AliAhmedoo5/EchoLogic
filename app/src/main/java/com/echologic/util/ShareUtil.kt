package com.echologic.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtil {

    /**
     * Generates a branded quote card image and shares it via the Android share sheet.
     */
    fun shareQuoteAsImage(
        context: Context,
        quoteText: String,
        author: String,
        category: String
    ) {
        val bitmap = renderQuoteCard(quoteText, author, category)
        val file = saveBitmapToCache(context, bitmap)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(
                Intent.EXTRA_TEXT,
                "\"$quoteText\" — $author\n\nShared via EchoLogic ⚡"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Quote"))
    }

    /**
     * Renders a quote card to a Bitmap using Android Canvas.
     * Creates a bold Neo-Brutalist style card with the app's signature look.
     */
    private fun renderQuoteCard(
        quoteText: String,
        author: String,
        category: String
    ): Bitmap {
        val width = 1080
        val height = 1350 // 4:5 aspect ratio (Instagram-friendly)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // ── Colors ──
        val bgColor = Color.parseColor("#0A0A0A")
        val primaryColor = Color.parseColor("#F4FF4D") // Electric Yellow
        val textColor = Color.WHITE
        val mutedColor = Color.parseColor("#888888")
        val accentPink = Color.parseColor("#FF6B9D")

        // ── Background ──
        canvas.drawColor(bgColor)

        // ── Subtle gradient overlay at top ──
        val gradientPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height * 0.4f,
                Color.parseColor("#1A1A2E"),
                bgColor,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height * 0.4f, gradientPaint)

        // ── Border (thick Neo-Brutalist style) ──
        val borderPaint = Paint().apply {
            color = primaryColor
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawRect(20f, 20f, width - 20f, height - 20f, borderPaint)

        val padding = 80f
        var y = 140f

        // ── Category Badge ──
        val categoryPaint = TextPaint().apply {
            color = primaryColor
            textSize = 32f
            typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.15f
        }
        val categoryText = category.uppercase()
        val categoryWidth = categoryPaint.measureText(categoryText)

        // Badge background
        val badgePaint = Paint().apply {
            color = primaryColor
            alpha = 30
            style = Paint.Style.FILL
        }
        val badgeBorderPaint = Paint().apply {
            color = primaryColor
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        val badgeRect = RectF(
            padding - 16f, y - 36f,
            padding + categoryWidth + 16f, y + 10f
        )
        canvas.drawRoundRect(badgeRect, 8f, 8f, badgePaint)
        canvas.drawRoundRect(badgeRect, 8f, 8f, badgeBorderPaint)
        canvas.drawText(categoryText, padding, y, categoryPaint)

        y += 80f

        // ── Opening Quote Mark ──
        val quoteMark = TextPaint().apply {
            color = primaryColor
            textSize = 120f
            typeface = Typeface.create("serif", Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("\u201C", padding - 10f, y + 40f, quoteMark)
        y += 80f

        // ── Quote Text ──
        val quotePaint = TextPaint().apply {
            color = textColor
            textSize = 52f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            isAntiAlias = true
            letterSpacing = 0.02f
        }
        val textWidth = (width - padding * 2).toInt()
        val quoteLayout = StaticLayout.Builder.obtain(
            quoteText, 0, quoteText.length, quotePaint, textWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(16f, 1.2f)
            .setMaxLines(12)
            .build()

        canvas.save()
        canvas.translate(padding, y)
        quoteLayout.draw(canvas)
        canvas.restore()

        y += quoteLayout.height + 60f

        // ── Author ──
        val authorPaint = TextPaint().apply {
            color = mutedColor
            textSize = 36f
            typeface = Typeface.create("sans-serif", Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("— $author", padding, y, authorPaint)

        // ── Branding at bottom ──
        val brandY = height - 80f

        // Separator line
        val linePaint = Paint().apply {
            color = primaryColor
            alpha = 80
            strokeWidth = 2f
        }
        canvas.drawLine(padding, brandY - 50f, width - padding, brandY - 50f, linePaint)

        // App name
        val brandPaint = TextPaint().apply {
            color = primaryColor
            textSize = 28f
            typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.3f
        }
        canvas.drawText("ECHOLOGIC", padding, brandY, brandPaint)

        // Tagline
        val taglinePaint = TextPaint().apply {
            color = mutedColor
            textSize = 22f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            isAntiAlias = true
        }
        val tagline = "Brain off. Logic on. ⚡"
        val taglineWidth = taglinePaint.measureText(tagline)
        canvas.drawText(tagline, width - padding - taglineWidth, brandY, taglinePaint)

        return bitmap
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
        val dir = File(context.cacheDir, "shared_quotes")
        dir.mkdirs()
        val file = File(dir, "echologic_quote.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }
}

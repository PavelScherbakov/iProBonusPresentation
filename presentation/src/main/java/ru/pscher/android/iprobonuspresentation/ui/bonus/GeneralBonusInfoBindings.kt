package ru.pscher.android.iprobonuspresentation.ui.common

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import ru.pscher.android.iprobonuspresentation.R

@BindingAdapter("formattedBonusDateTime")
fun TextView.formattedActivationDateTime(date: LocalDateTime?) {
    if(date == null) {
        this.visibility = View.GONE
        return
    } else {
        this.visibility = View.VISIBLE
    }

    val dateStr = date.format(DateTimeFormatter.ofPattern("dd.MM"))

    this.text = this.context.getString(R.string.bonus_date_template, dateStr)
}

@BindingAdapter("formattedBonusValue")
fun TextView.formattedBonusValue(value: Double?) {
    if(value == null) {
        this.visibility = View.GONE
        return
    } else {
        this.visibility = View.VISIBLE
    }

    this.text = this.context.getString(R.string.bonus_value_template, value)
}

// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "utils/FinanceUtils.h"
#include <QtMath>
#include <cmath>

QString incomeFrequencyToString(IncomeFrequency frequency) {
    switch (frequency) {
    case IncomeFrequency::Weekly: return "Weekly";
    case IncomeFrequency::Fortnightly: return "Fortnightly";
    case IncomeFrequency::Monthly: return "Monthly";
    case IncomeFrequency::Yearly: return "Yearly";
    case IncomeFrequency::OneOff: return "One-off";
    }
    return "Weekly";
}

IncomeFrequency incomeFrequencyFromString(const QString& value) {
    const auto v = value.trimmed().toLower();
    if (v == "fortnightly") return IncomeFrequency::Fortnightly;
    if (v == "monthly") return IncomeFrequency::Monthly;
    if (v == "yearly") return IncomeFrequency::Yearly;
    if (v == "one-off" || v == "one off" || v == "oneoff") return IncomeFrequency::OneOff;
    return IncomeFrequency::Weekly;
}

QString billFrequencyToString(BillFrequency frequency) {
    switch (frequency) {
    case BillFrequency::Weekly: return "Weekly";
    case BillFrequency::BiWeekly: return "Bi-Weekly";
    case BillFrequency::Monthly: return "Monthly";
    }
    return "Weekly";
}

BillFrequency billFrequencyFromString(const QString& value) {
    const auto v = value.trimmed().toLower();
    if (v == "bi-weekly" || v == "biweekly" || v == "fortnightly") return BillFrequency::BiWeekly;
    if (v == "monthly") return BillFrequency::Monthly;
    return BillFrequency::Weekly;
}

QString paymentMethodToString(BillPaymentMethod method) {
    return method == BillPaymentMethod::Automatic ? "automatic" : "manual";
}

BillPaymentMethod paymentMethodFromString(const QString& value) {
    return value.trimmed().toLower() == "automatic" ? BillPaymentMethod::Automatic : BillPaymentMethod::Manual;
}

QString formatDate(const QDate& date) {
    return date.isValid() ? date.toString("dd MMM yyyy") : QString();
}

QString formatOptionalDate(const std::optional<QDate>& date) {
    return date && date->isValid() ? date->toString("dd MMM yyyy") : QStringLiteral("N/A");
}

std::optional<QDate> parseOptionalDate(const QJsonObject& obj, const QString& key) {
    const auto value = obj.value(key);
    if (!value.isString()) return std::nullopt;
    const QDate date = QDate::fromString(value.toString(), Qt::ISODate);
    if (!date.isValid()) return std::nullopt;
    return date;
}

double weeksFromFrequency(IncomeFrequency frequency, double amount) {
    switch (frequency) {
    case IncomeFrequency::Weekly: return amount;
    case IncomeFrequency::Fortnightly: return amount / 2.0;
    case IncomeFrequency::Monthly: return amount * 12.0 / 52.0;
    case IncomeFrequency::Yearly: return amount / 52.0;
    case IncomeFrequency::OneOff: return amount;
    }
    return amount;
}

double weeklyExpenseContribution(BillFrequency frequency, double amount, bool spreadWeekly) {
    switch (frequency) {
    case BillFrequency::Weekly: return amount;
    case BillFrequency::BiWeekly: return amount / 2.0;
    case BillFrequency::Monthly: return spreadWeekly ? amount / 4.0 : 0.0;
    }
    return 0.0;
}

QString formatTimeframe(double weeks) {
    if (!std::isfinite(weeks) || weeks <= 0.0) {
        return "0 weeks";
    }
    const int totalWeeks = qCeil(weeks);
    const int months = totalWeeks / 4;
    const int remWeeks = totalWeeks % 4;
    QStringList parts;
    if (months > 0) parts << QString("%1 month%2").arg(months).arg(months == 1 ? "" : "s");
    if (remWeeks > 0) parts << QString("%1 week%2").arg(remWeeks).arg(remWeeks == 1 ? "" : "s");
    if (parts.isEmpty()) parts << "0 weeks";
    return parts.join(", ");
}

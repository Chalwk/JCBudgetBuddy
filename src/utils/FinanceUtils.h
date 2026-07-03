// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QString>
#include <QDate>
#include <QJsonObject>
#include <QJsonArray>
#include <optional>

enum class IncomeFrequency {
    Weekly,
    Fortnightly,
    Monthly,
    Yearly,
    OneOff
};

enum class BillFrequency {
    Weekly,
    BiWeekly,
    Monthly
};

enum class BillPaymentMethod {
    Manual,
    Automatic
};

QString incomeFrequencyToString(IncomeFrequency frequency);
IncomeFrequency incomeFrequencyFromString(const QString& value);

QString billFrequencyToString(BillFrequency frequency);
BillFrequency billFrequencyFromString(const QString& value);

QString paymentMethodToString(BillPaymentMethod method);
BillPaymentMethod paymentMethodFromString(const QString& value);

QString formatDate(const QDate& date);
QString formatOptionalDate(const std::optional<QDate>& date);
std::optional<QDate> parseOptionalDate(const QJsonObject& obj, const QString& key);

double weeksFromFrequency(IncomeFrequency frequency, double amount);
double weeklyExpenseContribution(BillFrequency frequency, double amount, bool spreadWeekly);

QString formatTimeframe(double weeks);

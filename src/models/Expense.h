// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QDate>
#include <QString>
#include <QJsonObject>

struct Expense
{
    QDate date;
    QString description;
    double amount = 0.0;
    QString category;

    QJsonObject toJson() const;
    static Expense fromJson(const QJsonObject &obj);
};
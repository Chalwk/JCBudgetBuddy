// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QList>
#include <QJsonObject>
#include "models/IncomeStream.h"
#include "models/Bill.h"
#include "models/Invoice.h"
#include "models/PlanItem.h"

struct UserData {
    QList<IncomeStream> incomes;
    QList<Bill> weeklyBills;
    QList<Bill> monthlyBills;
    QList<Invoice> invoices;
    QList<PlanItem> plans;

    QJsonObject toJson() const;
    static UserData fromJson(const QJsonObject& obj);
};

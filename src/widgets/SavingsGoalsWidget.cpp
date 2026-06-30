// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "widgets/SavingsGoalsWidget.h"
#include "data/DataManager.h"
#include "dialogs/ItemDialogs.h"
#include "models/PlanItem.h"

#include <QGroupBox>
#include <QLabel>
#include <QProgressBar>
#include <QPushButton>
#include <QFormLayout>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QMessageBox>
#include <QDate>

SavingsGoalsWidget::SavingsGoalsWidget(QWidget* parent)
    : QWidget(parent) {
    QVBoxLayout* mainLayout = new QVBoxLayout(this);
    mainLayout->setContentsMargins(0, 0, 0, 0);

    m_scrollArea = new QScrollArea(this);
    m_scrollArea->setWidgetResizable(true);
    m_scrollArea->setFrameShape(QFrame::NoFrame);

    m_container = new QWidget;
    m_containerLayout = new QVBoxLayout(m_container);
    m_containerLayout->setAlignment(Qt::AlignTop);
    m_containerLayout->setSpacing(12);

    m_scrollArea->setWidget(m_container);
    mainLayout->addWidget(m_scrollArea);

    connect(&DataManager::instance(), &DataManager::dataChanged,
            this, &SavingsGoalsWidget::refresh);

    refresh();
}

void SavingsGoalsWidget::refresh() {
    rebuildUI();
}

void SavingsGoalsWidget::rebuildUI() {
    QLayoutItem* child;
    while ((child = m_containerLayout->takeAt(0)) != nullptr) {
        delete child->widget();
        delete child;
    }

    const auto& plans = DataManager::instance().data().plans;
    if (plans.isEmpty()) {
        QLabel* emptyLabel = new QLabel("No savings goals defined. Add a plan in the 'Plans' tab.");
        emptyLabel->setAlignment(Qt::AlignCenter);
        m_containerLayout->addWidget(emptyLabel);
        return;
    }

    for (int i = 0; i < plans.size(); ++i) {
        const PlanItem& plan = plans[i];

        QGroupBox* group = new QGroupBox;
        group->setTitle(plan.name);
        QVBoxLayout* groupLayout = new QVBoxLayout(group);

        QProgressBar* progressBar = new QProgressBar;
        progressBar->setRange(0, 100);
        progressBar->setValue(static_cast<int>(plan.progressPercentage()));
        progressBar->setFormat(QString("%p%  ($%1 of $%2)")
                                   .arg(plan.totalPaid(), 0, 'f', 2)
                                   .arg(plan.totalCost, 0, 'f', 2));
        groupLayout->addWidget(progressBar);

        QFormLayout* infoLayout = new QFormLayout;
        infoLayout->setLabelAlignment(Qt::AlignRight);

        QLabel* totalLabel = new QLabel(QString("$%1").arg(plan.totalCost, 0, 'f', 2));
        QLabel* paidLabel = new QLabel(QString("$%1").arg(plan.totalPaid(), 0, 'f', 2));
        QLabel* remainingLabel = new QLabel(QString("$%1").arg(plan.remainingAfterPayments(), 0, 'f', 2));
        QLabel* completionLabel = new QLabel(plan.estimatedCompletionDate().isValid() ?
                                                 plan.estimatedCompletionDate().toString("dd MMM yyyy") :
                                                 "N/A (no weekly payment set)");

        infoLayout->addRow("Total cost:", totalLabel);
        infoLayout->addRow("Paid so far:", paidLabel);
        infoLayout->addRow("Remaining:", remainingLabel);
        infoLayout->addRow("Est. completion:", completionLabel);

        groupLayout->addLayout(infoLayout);

        QHBoxLayout* buttonLayout = new QHBoxLayout;
        buttonLayout->addStretch();

        QPushButton* logButton = new QPushButton("Log Payment");
        logButton->setProperty("planIndex", i);
        connect(logButton, &QPushButton::clicked, this, [this, i]() {
            logPaymentForPlan(i);
        });
        buttonLayout->addWidget(logButton);

        QPushButton* manageButton = new QPushButton("Manage Payments");
        manageButton->setProperty("planIndex", i);
        connect(manageButton, &QPushButton::clicked, this, [this, i]() {
            managePaymentsForPlan(i);
        });
        buttonLayout->addWidget(manageButton);

        groupLayout->addLayout(buttonLayout);

        m_containerLayout->addWidget(group);
    }
}

void SavingsGoalsWidget::logPaymentForPlan(int planIndex) {
    auto& plans = DataManager::instance().data().plans;
    if (planIndex < 0 || planIndex >= plans.size()) return;

    PaymentDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        Payment newPayment = dialog.resultData();
        plans[planIndex].addPayment(newPayment);

        QString error;
        if (!DataManager::instance().save(&error)) {
            QMessageBox::critical(this, "Save Error", error);
            return;
        }
        emit DataManager::instance().dataChanged();
    }
}

void SavingsGoalsWidget::managePaymentsForPlan(int planIndex) {
    auto& plans = DataManager::instance().data().plans;
    if (planIndex < 0 || planIndex >= plans.size()) return;

    PlanPaymentsDialog dialog(&plans[planIndex], this);
    dialog.exec();
}
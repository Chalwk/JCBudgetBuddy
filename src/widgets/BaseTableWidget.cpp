// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "widgets/BaseTableWidget.h"
#include <QDropEvent>
#include <QModelIndex>
#include <QHeaderView>

class ReorderableTableView : public QTableView {
    Q_OBJECT
public:
    explicit ReorderableTableView(QWidget* parent = nullptr)
        : QTableView(parent)
    {
        setDragEnabled(true);
        setAcceptDrops(true);
        setDropIndicatorShown(true);
        setDragDropMode(QAbstractItemView::InternalMove);
    }

signals:
    void rowsMoved(int fromRow, int toRow);

protected:
    void dropEvent(QDropEvent* event) override {
        QModelIndex droppedIndex = indexAt(event->position().toPoint());
        if (!droppedIndex.isValid()) {
            event->ignore();
            return;
        }
        int toRow = droppedIndex.row();
        QModelIndex current = currentIndex();
        if (!current.isValid()) {
            event->ignore();
            return;
        }
        int fromRow = current.row();
        if (fromRow == toRow) {
            event->ignore();
            return;
        }
        emit rowsMoved(fromRow, toRow);
        event->accept();
    }
};

BaseTableWidget::BaseTableWidget(QWidget* parent)
    : QWidget(parent) {}

void BaseTableWidget::setupTable(const QStringList& headers) {
    auto* layout = new QVBoxLayout(this);
    auto* reorderTable = new ReorderableTableView(this);
    m_table = reorderTable;
    m_model = new QStandardItemModel(this);
    m_model->setHorizontalHeaderLabels(headers);
    m_table->setModel(m_model);
    m_table->setSelectionBehavior(QAbstractItemView::SelectRows);
    m_table->setSelectionMode(QAbstractItemView::SingleSelection);
    m_table->setEditTriggers(QAbstractItemView::NoEditTriggers);
    m_table->horizontalHeader()->setStretchLastSection(true);
    m_table->verticalHeader()->setVisible(false);
    layout->addWidget(m_table);

    connect(reorderTable, &ReorderableTableView::rowsMoved,
            this, &BaseTableWidget::rowsMoved);
}

void BaseTableWidget::setupButtons(const QString& addText, const QString& editText, const QString& deleteText) {
    auto* layout = qobject_cast<QVBoxLayout*>(this->layout());
    auto* row = new QHBoxLayout;
    m_addButton = new QPushButton(addText, this);
    m_editButton = new QPushButton(editText, this);
    m_deleteButton = new QPushButton(deleteText, this);
    row->addWidget(m_addButton);
    row->addWidget(m_editButton);
    row->addWidget(m_deleteButton);
    row->addStretch();
    layout->addLayout(row);
}

int BaseTableWidget::selectedRow() const {
    const QModelIndex index = m_table->currentIndex();
    return index.isValid() ? index.row() : -1;
}

#include "BaseTableWidget.moc"
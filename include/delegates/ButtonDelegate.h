#pragma once

#include <QStyledItemDelegate>

class ButtonDelegate : public QStyledItemDelegate {
    Q_OBJECT

public:
    explicit ButtonDelegate(const QString& text, QObject* parent = nullptr);

    void paint(QPainter* painter, const QStyleOptionViewItem& option, const QModelIndex& index) const override;
    bool editorEvent(QEvent* event, QAbstractItemModel* model, const QStyleOptionViewItem& option, const QModelIndex& index) override;

signals:
    void clicked(const QModelIndex& index);

private:
    QString m_text;
};

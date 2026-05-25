-- Script này dùng để cập nhật bảng Vouchers, thêm 2 cột Quantity và RemainingQuantity

USE ecommerce_db;
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Vouchers]') AND name = 'quantity')
BEGIN
    ALTER TABLE [dbo].[Vouchers] ADD quantity int DEFAULT 0;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Vouchers]') AND name = 'remaining_quantity')
BEGIN
    ALTER TABLE [dbo].[Vouchers] ADD remaining_quantity int DEFAULT 0;
END
GO

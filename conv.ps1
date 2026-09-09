Add-Type -AssemblyName System.Drawing
$img = [System.Drawing.Image]::FromFile('C:\Users\Admin\.gemini\antigravity\brain\0c1bdc2a-9310-477b-ae65-2f9d8dfca964\.user_uploaded\media_1787728834279.webp')
$img.Save('C:\Users\Admin\Downloads\QuanLyTuyenSinh_CNJ02\QuanLyTuyenSinh\resources\logo-eaut.png', [System.Drawing.Imaging.ImageFormat]::Png)

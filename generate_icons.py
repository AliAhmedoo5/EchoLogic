from PIL import Image
import os

source = r"C:\Users\pds\.gemini\antigravity\brain\e15a6d54-1c2b-404c-b2a5-8c2d9f87aadd\media__1778173617214.jpg"
res_base = r"e:\Projects\dribile\sp_dribbile\app\src\main\res"

img = Image.open(source).convert("RGBA")

# Crop to just the icon (remove the text at the bottom and transparent borders)
# The icon area is roughly the top 85% of the image, centered
w, h = img.size
# Crop bottom text "RESOLUTION: 1024x1024 | FORMAT..." 
crop_bottom = int(h * 0.06)  # bottom ~6% is text
img = img.crop((0, 0, w, h - crop_bottom))

# Now make it square by taking the center square
w, h = img.size
side = min(w, h)
left = (w - side) // 2
top = (h - side) // 2
img = img.crop((left, top, left + side, top + side))

# Replace white/near-white/transparent checkerboard background with transparency
# The icon has a checkered background (transparency indicator) that we need to remove
from PIL import ImageDraw
pixels = img.load()
for y in range(img.height):
    for x in range(img.width):
        r, g, b, a = pixels[x, y]
        # Detect checkerboard pattern (light gray ~204 and white ~255)
        if (r > 190 and g > 190 and b > 190):
            pixels[x, y] = (0, 0, 0, 0)  # Make transparent

# Save the cleaned icon at full resolution for reference
img.save(os.path.join(res_base, "..", "..", "..", "app_icon_clean.png"))

# --- Generate foreground for adaptive icon (432x432 with padding) ---
# Adaptive icons use a 108dp canvas where the icon should be in the center 72dp (66%)
# So for foreground, we add ~18% padding on each side
fg_size = 432  # Standard foreground size
icon_area = int(fg_size * 0.66)  # 66% of canvas = ~285px
padding = (fg_size - icon_area) // 2

# Resize icon to fit the center area
icon_resized = img.resize((icon_area, icon_area), Image.LANCZOS)

# Create foreground with transparent background
foreground = Image.new("RGBA", (fg_size, fg_size), (0, 0, 0, 0))
foreground.paste(icon_resized, (padding, padding), icon_resized)

# --- Generate mipmap density directories ---
densities = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

fg_densities = {
    "mipmap-mdpi": 108,
    "mipmap-hdpi": 162,
    "mipmap-xhdpi": 216,
    "mipmap-xxhdpi": 324,
    "mipmap-xxxhdpi": 432,
}

for density_dir, size in densities.items():
    dir_path = os.path.join(res_base, density_dir)
    os.makedirs(dir_path, exist_ok=True)
    
    # ic_launcher.png (regular icon - square with rounded corners will be applied by launcher)
    resized = img.resize((size, size), Image.LANCZOS)
    resized.save(os.path.join(dir_path, "ic_launcher.png"))
    
    # ic_launcher_round.png (same, launchers apply circular mask)
    resized.save(os.path.join(dir_path, "ic_launcher_round.png"))

for density_dir, size in fg_densities.items():
    dir_path = os.path.join(res_base, density_dir)
    os.makedirs(dir_path, exist_ok=True)
    
    # Foreground layer for adaptive icon
    fg_resized = foreground.resize((size, size), Image.LANCZOS)
    fg_resized.save(os.path.join(dir_path, "ic_launcher_foreground.png"))

print("Icon generation complete!")
for d in densities:
    path = os.path.join(res_base, d)
    files = os.listdir(path)
    print(f"  {d}: {files}")

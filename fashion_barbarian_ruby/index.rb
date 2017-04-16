require 'net/http'
require 'json'

puts "Welcome to Fashion Barbarian!"
puts "-----------------------------"
puts "Here is your random trendy product under $50:"

trendy_keywords = [
  "ruffle",
  "off the shoulder",
  "floral",
  "90s",
  "safari chic"
]

trendy_keyword = trendy_keywords.sample

uri = URI("http://api.shopstyle.com/api/v2/products")
params = {
  :pid => ENV["SHOPSTYLE_API_KEY"],
  :fts => trendy_keyword,
  :offset => 0,
  :limit => 5
}
price_filter_pararms = "&fl=p7&fl=p8"
uri.query = URI.encode_www_form(params) + price_filter_pararms

begin
  response = Net::HTTP.get_response(uri)
  res = JSON.parse(response.body)
  random_product  = rand(5)
  product         = res.dig("products").dig(random_product)
  product_name    = product.dig("name")
  retail_price    = product.dig("priceLabel")
  sale_price      = product.dig("salePriceLabel") || "Not on sale :("
  product_url    = res.dig("products").dig(random_product).dig("clickUrl")
  puts product_name
  puts "Retail Price: #{retail_price}"
  puts "Sale Price: #{sale_price}"
  puts product_url
rescue
  "Sorry, there was an error. Please try again."
end

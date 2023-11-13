#this script looks for stock tickers locally before running pipeline


import argparse
import os 
from src.utils.functions import load_client, lookup_ticker

if __name__ == '__main__':
    finnhub_client = load_client('')    # use this to run locally, one below for non-local run
    #finnhub_client = load_client(os.environ['FINNHUB_API_TOKEN'])

    parser = argparse.ArgumentParser(description="Get list of tickers based on Finnhub search",
                                     prog="ticker_search.py",
                                     formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument('--ticker', type=str, help="Enter the phrase to look for a ticker")

    args = parser.parse_args()
    params = vars(args)

    try:
        print(lookup_ticker(finnhub_client, params['ticker']))
    except Exception as e:
        print(str(e))

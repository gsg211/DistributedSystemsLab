from abc import ABC, abstractmethod

class AuctionStrategy(ABC):
    @abstractmethod
    def determine_winner(self, bids):
        pass

    @abstractmethod
    def is_bid_valid(self, bid, context):
        pass

class EnglishStrategy(AuctionStrategy):
    """Licitație ascendentă: Cel mai mare preț câștigă."""
    def is_bid_valid(self, bid, current_max):
        return bid['amount'] > current_max

    def determine_winner(self, bids):
        if not bids: return None
        return max(bids, key=lambda x: x['amount'])

class DutchStrategy(AuctionStrategy):
    """Licitație descendentă: Primul care acceptă prețul câștigă."""
    def is_bid_valid(self, bid, current_price):
        return bid['amount'] >= current_price

    def determine_winner(self, bids):
        if not bids: return None
        # Primul venit conform timestamp
        return min(bids, key=lambda x: x['timestamp'])

class AuctionStrategyFactory:
    @staticmethod
    def create_strategy(strategy_type):
        strategies = {
            "English": EnglishStrategy(),
            "Dutch": DutchStrategy()
            # Se pot adăuga CandleStrategy, SwedishStrategy aici
        }
        return strategies.get(strategy_type, EnglishStrategy())
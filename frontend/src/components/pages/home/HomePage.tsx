import { Link } from "react-router-dom";
import { Carousel } from "./components/Carousel";
import { useAuthenticationContext } from "../../../authentication/authenticationContext";

export const HomePage = () => {
    const { authentication } = useAuthenticationContext();
    const isLoggedIn = authentication.isAuthenticated;

    return (
        <div className="page-container">
            <div className="container-centered px-5">

                {/* Welcome Section */}
                <div className="text-center mb-12">
                    <h1 className="text-4xl font-bold text-gray-800 mb-4">
                        Welcome to BookStore
                    </h1>
                    <p className="text-lg text-gray-600 max-w-2xl mx-auto">
                        Discover your next favorite book from our curated collection of amazing reads.
                    </p>
                    {!isLoggedIn && (
                        <div className="mt-6 flex justify-center gap-4">
                            <Link
                                to="/login"
                                className="bg-teal-600 hover:bg-teal-700 text-white font-medium py-2 px-6 rounded-lg transition-colors"
                            >
                                Sign In
                            </Link>
                            <Link
                                to="/register"
                                className="bg-white border border-gray-300 hover:bg-gray-50 text-gray-700 font-medium py-2 px-6 rounded-lg transition-colors"
                            >
                                Register
                            </Link>
                        </div>
                    )}
                </div>

                {/* Featured Books Carousel */}
                <section className="mb-12">
                    <div className="flex items-center justify-between mb-6">
                        <h2 className="text-2xl font-bold text-gray-800">📚 Featured Books</h2>
                        <Link
                            to="/search"
                            className="text-teal-600 hover:text-teal-700 font-medium transition-colors"
                        >
                            View All →
                        </Link>
                    </div>
                    <Carousel />
                </section>

                {/* Quick Links */}
                <section className={`grid grid-cols-1 gap-6 ${isLoggedIn ? 'md:grid-cols-3' : 'md:grid-cols-1 max-w-md mx-auto'}`}>
                    <Link
                        to="/search"
                        className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow group"
                    >
                        <div className="text-3xl mb-3">🔍</div>
                        <h3 className="text-lg font-semibold text-gray-800 group-hover:text-teal-600 transition-colors">
                            Browse Books
                        </h3>
                        <p className="text-sm text-gray-600 mt-1">
                            Explore our entire collection
                        </p>
                    </Link>

                    {isLoggedIn && (
                        <>
                            <Link
                                to="/shelf"
                                className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow group"
                            >
                                <div className="text-3xl mb-3">📖</div>
                                <h3 className="text-lg font-semibold text-gray-800 group-hover:text-teal-600 transition-colors">
                                    My Bookshelf
                                </h3>
                                <p className="text-sm text-gray-600 mt-1">
                                    View your checked out books
                                </p>
                            </Link>

                            <Link
                                to="/discussions"
                                className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow group"
                            >
                                <div className="text-3xl mb-3">💬</div>
                                <h3 className="text-lg font-semibold text-gray-800 group-hover:text-teal-600 transition-colors">
                                    Discussions
                                </h3>
                                <p className="text-sm text-gray-600 mt-1">
                                    Join the community conversation
                                </p>
                            </Link>
                        </>
                    )}
                </section>

            </div>
        </div>
    );
};

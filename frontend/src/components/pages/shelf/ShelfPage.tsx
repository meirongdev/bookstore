import { Navigate } from "react-router-dom";
import { useAuthenticationContext } from "../../../authentication/authenticationContext";
import { useState } from "react";
import { HistoryTab } from "./components/HistoryTab";
import { CheckoutsTab } from "./components/CheckoutsTab";

type TabType = 'checkouts' | 'history';

export const ShelfPage = () => {

    const { authentication } = useAuthenticationContext();
    const [activeTab, setActiveTab] = useState<TabType>('checkouts');

    if (!authentication.isAuthenticated) {
        return <Navigate to={"/"} />
    }

    const tabs = [
        { id: 'checkouts' as TabType, label: 'My Checkouts', icon: '📚' },
        { id: 'history' as TabType, label: 'History', icon: '📋' }
    ];

    return (

        <div className="page-container">

            <div className="container-centered px-5">

                <h1 className="text-3xl font-bold text-gray-800 mb-6">My Bookshelf</h1>

                <div className="flex flex-col lg:flex-row gap-6">

                    {/* 侧边栏 Tabs */}
                    <div className="lg:w-48 flex-shrink-0">
                        <nav className="flex lg:flex-col gap-2">
                            {tabs.map(tab => (
                                <button
                                    key={tab.id}
                                    onClick={() => setActiveTab(tab.id)}
                                    className={`
                                        flex items-center gap-3 px-4 py-3 rounded-lg
                                        font-medium transition-all duration-200
                                        ${activeTab === tab.id
                                            ? 'bg-gray-800 text-white shadow-md'
                                            : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-200'
                                        }
                                    `}
                                >
                                    <span className="text-xl">{tab.icon}</span>
                                    <span>{tab.label}</span>
                                </button>
                            ))}
                        </nav>
                    </div>

                    {/* 内容区域 */}
                    <div className="flex-1 min-w-0">
                        {activeTab === 'checkouts' && <CheckoutsTab />}
                        {activeTab === 'history' && <HistoryTab />}
                    </div>

                </div>

            </div>

        </div>

    )

}

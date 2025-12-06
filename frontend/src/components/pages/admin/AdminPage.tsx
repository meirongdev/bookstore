import { Navigate } from "react-router-dom";
import { useAuthenticationContext } from "../../../authentication/authenticationContext";
import { useState } from "react";
import { AddBookTab } from "./components/AddBookTab";
import { QuantitiesTab } from "./components/QuantitiesTab";
import { DiscussionsTab } from "./components/DiscussionsTab";

type TabType = 'add-book' | 'quantities' | 'discussions';

export const AdminPage = () => {

    const { authentication } = useAuthenticationContext();
    const [activeTab, setActiveTab] = useState<TabType>('add-book');

    if (!authentication.isAuthenticated || authentication.authority !== "ROLE_ADMIN") {
        return <Navigate to={"/"} />
    }

    const tabs = [
        { id: 'add-book' as TabType, label: 'Add Book', icon: '📚' },
        { id: 'quantities' as TabType, label: 'Quantities', icon: '📊' },
        { id: 'discussions' as TabType, label: 'Discussions', icon: '💬' }
    ];

    return (

        <div className="page-container">

            <div className="container-centered px-5">

                <h1 className="text-3xl font-bold text-gray-800 mb-6">Admin Dashboard</h1>

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
                        {activeTab === 'add-book' && <AddBookTab />}
                        {activeTab === 'quantities' && <QuantitiesTab />}
                        {activeTab === 'discussions' && <DiscussionsTab />}
                    </div>

                </div>

            </div>

        </div>

    )
}

import DOMPurify from "dompurify";
import React from "react";
import { FaCircle, FaComment, FaShare } from "react-icons/fa";
import { Link } from "react-router-dom";
import { format } from "timeago.js";
import { BASE_PRODUCTIMG } from "../../constants/constants";
import { formatPostHTMLWithDiscount } from "../../helpers/helpers";

const PostItem = ({ data, hidePinned, className }) => {
  const post = {
    _id: data?.id ?? "",
    title: data?.title ?? "",
    image: data?.textImg ?? BASE_PRODUCTIMG,
    description: data?.content ?? "",
    createDate: data?.createDate && format(data?.createDate),
    updateDate: data?.updateDate && format(data?.updateDate),
    price: data?.price ?? 0,
    rates: data?.rates ?? [],
    pinned: data?.pinned ?? false,
    rateCount: data?.rateCount ?? 0,
  };
  if(hidePinned && post.pinned) return <></>;
  //   {"id":11,"title":"Trends in Gold Jewelry","content":"Discussing the latest trends in gold jewelry.",
  //   "createDate":"2024-03-12","updateDate":"2024-03-12","deleteDate":null,
  // "textImg":null,"rates":[],"hide":false,"pinned":false}
  // const content ='<div className="richtext-container css-6wiuj0" style="white-space: pre-wrap; overflow-wrap: break-word;"><p className=" richtext-paragraph  css-srnt4i"><span data-bn-type="text" className="richtext-text css-1iqe90x">According to CryptoPotato, the volume of daily large transactions on the decentralized, open-source Proof-of-Stake blockchain Avalanche has surged to its highest since December 2023. The network’s native token, </span><a data-bn-type="text" className="richtext-coinpair css-10nd978" target="_blank" href="/en/trade/AVAX_USDT">AVAX</a><span data-bn-type="text" className="richtext-text css-1iqe90x">, reached levels not seen since May 2022. Crypto market intelligence platform IntoTheBlock reported that Avalanche’s daily large transaction volume hit $329 million on Monday, March 11, indicating increased activity of large holders of AVAX.</span></p><p className=" richtext-paragraph richtext-paragraph-empty css-srnt4i"></p><p className=" richtext-paragraph  css-srnt4i"><span data-bn-type="text" className="richtext-text css-1iqe90x">The last time large AVAX transactions, those with a value of $100,000 or more, recorded volumes around the current figure was on December 29, when the amount sat at $412 million. At the time, AVAX was worth $48, and the number of large transactions was more than 2,800. Data from IntoTheBlock shows that the number of large AVAX transactions is experiencing another recovery. While Avalanche’s current large transaction volume is far from the peak reached in the last bull run, the uptick indicates a bullish trend and a positive sign for market participants.</span></p><p className=" richtext-paragraph richtext-paragraph-empty css-srnt4i"></p><p className=" richtext-paragraph  css-srnt4i"><span data-bn-type="text" className="richtext-text css-1iqe90x">The increase in large </span><a data-bn-type="text" className="richtext-coinpair css-10nd978" target="_blank" href="/en/trade/AVAX_USDT">AVAX</a><span data-bn-type="text" className="richtext-text css-1iqe90x"> transaction volumes comes within a month after the Avalanche network experienced an hours-long outage. On February 23, the network’s C-Chain stopped producing blocks due to a finalization issue resulting from a client code bug. Developers eventually resolved the issue, and the blockchain resumed normal operations after roughly five hours. The incident affected AVAX as the asset’s price plunged by more than 3%, closing that day at $35. Meanwhile, AVAX has recovered significantly, becoming one of the top-performing altcoins in the market recently, alongside Solana (</span><a data-bn-type="text" className="richtext-coinpair css-10nd978" target="_blank" href="/en/trade/SOL_USDT">SOL</a><span data-bn-type="text" className="richtext-text css-1iqe90x">), Toncoin (TON), and Algorand (</span><a data-bn-type="text" className="richtext-coinpair css-10nd978" target="_blank" href="/en/trade/ALGO_USDT">ALGO</a><span data-bn-type="text" className="richtext-text css-1iqe90x">).</span></p></div>';
  return (
    <div key={post._id} className={className ?? `col-12 col-xl-6 my-3 p-2`}>
      <div className="card p-3 rounded rounded-2 ">
        <div className="d-flex flex-row align-items-start  ">
          <img
            className="card-img-top me-auto img-fluid"
            style={{ width: "20%", objectFit: "cover", aspectRatio: 1 }}
            src={post.image}
            alt={post.title}
          />
          <Link
            to={`/forums/${post._id}`}
            className="card-body ps-3 p-1 d-flex justify-content-center flex-column position-relative text-decoration-none overflow-hidden"
          >
            <div className="mb-1 card-text small">
              <FaCircle
                size={10}
                className="orange"
                style={{ marginTop: "-3px", marginRight: "5px" }}
              />
              {post?.createDate}
            </div>
            <h5 className="card-title ">
              <span>{post.title}</span>
            </h5>
            <div className="ratings d-flex"></div>
            <div className="card-description mb-auto pt-2 line-clamp-4" style={{height:"calc(1.4em * 5)"}}>
              {DOMPurify.sanitize(formatPostHTMLWithDiscount( post?.description), {
                ALLOWED_TAGS: [], // Allow no tags
                ALLOWED_ATTR: [], // Allow no attributes
              }).toString()}
            </div>
          </Link>
        </div>
        <div className="mt-auto pt-2 row">
          <div className="col" />
          <div
            className="list-group-horizontal   d-inline-flex gap-2"
            style={{ width: "fit-content" }}
          >
            <Link
              to={`/forums/${post._id}#rating`}
              className="d-inline-block text text-decoration-none  "
            >
              <span className="small me-2">{post.rateCount}</span>
              <FaComment className="icon clickable" />
            </Link>
            <section className="d-inline-block  ">
              <span
                className="small me-2 user-select-none "
                style={{ color: "transparent" }}
              >
                {post.rateCount}
              </span>
              <FaShare className="icon clickable" />
            </section>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostItem;
